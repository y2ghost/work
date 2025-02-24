# coding=utf8

from twisted.logger import Logger

# 处理Protocol对象的会话状态机
class StateMachine:
    log = Logger(namespace="StateMachine")

    def __init__(self):
        self.handlers = {}
        self.currState = None

    # 绑定给定状态的处理函数，该函数参数分别是Protocol对象、待处理数据、当前状态
    def addState(self, state, handler):
        self.handlers[state] = handler
        self.log.debug(f"state:{state} bound handler:{handler}")

    # 设置状态机的初始状态
    def SetInitializationState(self, state):
        self.currState = state
        self.log.debug(f"set initialization state:{state}")

    def handle(self, protocol, data):
        currState = self.currState
        handler = self.handlers[currState]
        self.currState = handler(protocol, data, currState)
        self.log.debug(f"handle state:{currState} -> {self.currState}")

