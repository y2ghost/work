# coding=utf8

import struct
from message import Message
from state import SessionState
from device_type import DeviceType

# 状态通用处理函数
# 状态函数如果没有触发状态转换则保持当前状态


class StateHandler:
    # INIT 状态的处理函数
    # INIT -> (CONNECTED)
    @staticmethod
    def initHandler(protocol, data, currState):
        state = currState
        if protocol.isOnline:
            state = SessionState.CONNECTED

        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return state

    # CONNECTED 状态的处理函数
    # CONNECTED -> (AUTHED, BOUND, DISCONNECT)
    # 认证数据格式不满足，状态不变，并告诉客户端错误
    # 认证通过则进入AUTHED状态，但是AP设备直接进入BOUND状态
    # 格式: hshs[deviceType][userId][deviceId]
    # 字节序网络序，deviceType、userId、deviceId均为整数，长度4字节
    @staticmethod
    def connectedHandler(protocol, data, currState):
        state = currState
        response = Message.AUTH_FAIL.value

        if not protocol.isOnline:
            state = SessionState.DISCONNECT
        elif 16 != len(data):
            protocol.sendString(response)
            protocol.log.info(f"reply {response} to client")
        else:
            authInfo = struct.unpack("!4sIII", data)
            authPrefix = authInfo[0]
            deviceType = None

            try:
                deviceType = DeviceType(authInfo[1])
            except ValueError as err:
                protocol.log.error(f"dervice type error: {err}")

            userId = authInfo[2]
            deviceId = authInfo[3]
            protocol.log.info(f"auth infomation: {authInfo}")

            # 认证成功后，按照设备类型，选择客户端的ID值，填充相关认证信息
            if deviceType is not None and authPrefix == Message.AUTH_PREFIX.value \
                    and protocol.checkAuthInfo(deviceType, userId, deviceId):
                protocol.deviceType = deviceType
                protocol.userId = userId
                protocol.deviceId = deviceId

                clientId = None
                if deviceType in (DeviceType.PHONE, DeviceType.IPAD):
                    clientId = userId
                elif DeviceType.AP == deviceType:
                    clientId = deviceId
                else:
                    raise ValueError("Impossible Client Id Value Be None")

                protocol.factory.authedClients[clientId] = protocol
                if DeviceType.AP == deviceType:
                    state = SessionState.BOUND
                else:
                    state = SessionState.AUTHED

                response = Message.AUTH_OK.value

            protocol.sendString(response)
            protocol.log.info(f"reply {response} to client")

        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return state

    # DISCONNECT 状态的处理函数
    # DISCONNECT -> ()  属于终止状态，不做任何事情
    @staticmethod
    def disconnectHandler(protocol, data, currState):
        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return currState

    # AUTHED 状态的处理函数(移动设备特有)
    # AUTHED -> (BOUND, DISCONNECT)
    # 用户设备发送 connect_[deviceId]
    # 字节序网络序 deviceId是4字节整数，表示AP ID值
    @staticmethod
    def deviceAuthedHandler(protocol, data, currState):
        state = currState
        response = Message.CONNECT_FAIL.value

        if not protocol.isOnline:
            state = SessionState.DISCONNECT
        elif 12 != len(data):
            protocol.sendString(response)
            protocol.log.info(f"reply {response} to client")
        else:
            connectInfo = struct.unpack("!8sI", data)
            connectPrefix = connectInfo[0]
            apId = connectInfo[1]
            protocol.log.info(f"connect infomation: {connectInfo}")

            # 检查请求的格式信息和对应的AP是否已连接
            if connectPrefix == Message.CONNECT_PREFIX.value and \
                    apId in protocol.factory.stationFactory.authedClients:
                protocol.apId = apId
                state = SessionState.BOUND
                response = Message.CONNECT_OK.value
            else:
                protocol.log.error("connect ap failed!")

            protocol.sendString(response)
            protocol.log.info(f"reply {response} to client")

        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return state

    # AUTHED 状态的处理函数(AP设备特有)
    # AUTHED -> (DISCONNECT)
    # 此状态下处理AP设备数据
    @staticmethod
    def apAuthedHandler(protocol, data, currState):
        state = currState
        if not protocol.isOnline:
            state = SessionState.DISCONNECT

        if isinstance(data, bytes):
            protocol.handleData(data, currState)

        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return state

    # BOUND 状态的处理函数(移动设备特有)
    # BOUND -> (DISCONNECT)
    # 此状态下处理移动设备数据
    @staticmethod
    def deviceBoundHandler(protocol, data, currState):
        state = currState
        if not protocol.isOnline:
            state = SessionState.DISCONNECT

        if isinstance(data, bytes):
            protocol.handleData(data, currState)

        protocol.log.debug(f"currState:{currState}, outState:{state}")
        return state
