# coding=utf8
# 本程序类似代理服务，将PHONE、IPAD的数据发送给AP，然后将AP的相应数据发送回原始设备

from fsm import StateMachine
from state import SessionState
from device_type import DeviceType
from state_handler import StateHandler
from tcp_data import parseData
from tcp_data import dataHandler
from twisted.logger import Logger
from twisted.internet.protocol import Factory
from twisted.protocols.basic import Int32StringReceiver

class BaseCommunication(Int32StringReceiver):
    MAX_LENGTH = 1024
    log = Logger(namespace="BaseCommunication")

    def __init__(self, factory, addr):
        self.factory = factory
        self.addr = addr
        self.isOnline = False
        # 值类型: DeviceType
        self.deviceType = None
        self.userId = None
        self.deviceId = None
        # 绑定AP ID，用于发送数据
        self.apId = None
        # 初始化通用状态机
        self.stateMachine = StateMachine()
        self.stateMachine.SetInitializationState(SessionState.INIT)
        self.stateMachine.addState(SessionState.INIT, StateHandler.initHandler)
        self.stateMachine.addState(SessionState.CONNECTED, StateHandler.connectedHandler)
        self.stateMachine.addState(SessionState.DISCONNECT, StateHandler.disconnectHandler)

    # 根据自身的需求验证身份信息，被状态机使用，返回布尔值
    def checkAuthInfo(self, deviceType, userId, deviceId):
        raise NotImplementedError

    # 根据自身的需求处理数据，被状态机使用位，无需返回值
    def handleData(self, data, currState):
        raise NotImplementedError

    # 连接建立后回调该函数，子类如果复写，需要显示调用本函数
    def connectionMade(self):
        self.isOnline = True
        self.stateMachine.handle(self, None)
        self.factory.numConnections += 1
        self.factory.clients.append(self)
        self.log.info(f"active connections: {self.factory.numConnections}")

    # 连接断开后回调该函数，子类如果复写，需要显示调用本函数
    def connectionLost(self, reason):
        self.factory.numConnections -= 1
        self.log.info(f"connection lost: {reason.getErrorMessage()}")
        self.log.info(f"active connections: {self.factory.numConnections}")
        self.factory.clients.remove(self)

        # 根据设备类型，删除工厂维护的连接
        clientId = None
        if self.deviceType in (DeviceType.PHONE, DeviceType.IPAD):
            clientId = self.userId
        elif DeviceType.AP == self.deviceType:
            clientId = self.deviceId
        else:
            clientId = None

        # 只有认证过的客户端才有必要删除
        if clientId is not None:
            del self.factory.authedClients[clientId]

        self.isOnline = False
        self.stateMachine.handle(self, None)

    # 父类dataReceived使用的回调函数 ，不要覆写除非清楚工作机制
    def stringReceived(self, data):
        self.stateMachine.handle(self, data)

    def lengthLimitExceeded(self, length):
        self.log.error(f"data length<{length}> more than {self.MAX_LENGTH}")
        self.transport.loseConnection()

class DeviceCommunication(BaseCommunication):
    log = Logger(namespace="DeviceCommunication")

    def __init__(self, factory, addr):
        super().__init__(factory, addr)
        self.stateMachine.addState(SessionState.AUTHED, StateHandler.deviceAuthedHandler)
        self.stateMachine.addState(SessionState.BOUND, StateHandler.deviceBoundHandler)

    # 验证设备类型: (DeviceType.PHONE，DeviceType.IPAD)
    def checkAuthInfo(self, deviceType, userId, deviceId):
        result = False
        if deviceType in (DeviceType.PHONE, DeviceType.IPAD):
            result = True
        else:
            self.log.error(f"deviceType: {deviceType} invalid")

        return result

    # 处理移动设备的数据
    # 先检查AP连接是否存在，然后转发数据给它
    def handleData(self, data, currState):
        # 存储数据
        dataInfo = parseData(data)
        if dataInfo:
            dataHandler(dataInfo)
        else:
            self.log.error(f"parse user device data failed")

        # 转发数据
        # 如果是便携式终端设备只存储数据，不做转发，结束数据处理
        if DeviceType.IPAD == self.deviceType:
            return

        authedStations = self.factory.apFactory.authedClients
        stationProtocol = authedStations.get(self.apId)

        if stationProtocol:
            stationProtocol.sendString(data)
        else:
            self.log.error(f"send data to station<{self.apId}> failed")

class ApCommunication(BaseCommunication):
    log = Logger(namespace="ApCommunication")

    def __init__(self, factory, addr):
        super().__init__(factory, addr)
        self.stateMachine.addState(SessionState.AUTHED, StateHandler.apAuthedHandler)

    # 验证设备类型: DeviceType.GROUND_STATION
    # 验证设备ID和用户ID存在数据库中 TODO
    # 验证用户ID恒为0
    def checkAuthInfo(self, deviceType, userId, deviceId):
        if DeviceType.AP != deviceType:
            self.log.error(f"deviceType: {deviceType} invalid")
            return False

        if 0x0 != userId:
            self.log.error(f"userId must be 0x0000")
            return False

        return True

    # 处理来自AP设备的数据
    def handleData(self, data, currState):
        # 存储数据
        dataInfo = parseData(data)
        if dataInfo:
            dataHandler(data)
        else:
            self.log.error(f"parse station data failed")
            return

        # 转发数据
        deviceProtocol.sendString(data)


class CommunicationFactory(Factory):
    # 处理用户设备连接的工厂对象
    deviceFactory = None
    # 处理地面站设备连接的工厂对象
    apFactory = None

    def __init__(self):
        # 维护已连接的客户端
        self.clients = []
        # 维护已认证的客户端，根据客户端的ID关联
        self.authedClients = {}
        # 本实例维护的所有连接数量
        self.numConnections = 0

# 处理用户设备连接工厂类
class DeviceFactory(CommunicationFactory):
    def __init__(self):
        super().__init__()
        CommunicationFactory.deviceFactory = self

    def buildProtocol(self, addr):
        return DeviceCommunication(self, addr)

# 处理地面站设备连接工厂类
class apFactory(CommunicationFactory):
    def __init__(self):
        super().__init__()
        CommunicationFactory.apFactory = self

    def buildProtocol(self, addr):
        return ApCommunication(self, addr)

