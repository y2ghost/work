# coding=utf8

from enum import Enum

# 会话状态信息
class SessionState(Enum):
    INIT = 0        # 初始化状态
    CONNECTED = 1   # 已连接状态
    DISCONNECT = 2  # 断开连接
    AUTHED = 3      # 已认证状态
    BOUND = 4       # 已绑定AP状态

