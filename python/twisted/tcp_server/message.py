# coding=utf8

from enum import Enum

# 设备认证、绑定交互等信息
class Message(Enum):
    AUTH_OK = b'auth_ok'
    AUTH_FAIL = b'auth_fail'
    AUTH_PREFIX = b'device'
    CONNECT_OK = b'connect_ok'
    CONNECT_FAIL = b'connect_fail'
    CONNECT_PREFIX = b'connect_'

