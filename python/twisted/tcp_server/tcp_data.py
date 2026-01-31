# coding=utf8

from twisted.internet.defer import Deferred

# 维护数据的分析器和处理器
_parsers = ()
_handlers  = ()

# 分析数据，返回值是个字典对象
# 如果分析不成功，返回None值
def parseData(data):
    result = None
    for parser in _parsers:
        result = parser(data)
        if result is not None:
            break

    return result

# 异步存储数据
# 注册了多个处理器函数
# 如果对应的处理器匹配了数据则返回None值
# 接受到None处理器函数无需处理也直接返回None
def dataHandler(data):
    d = Deferred()
    for handler in _handlers:
        d.addCallback(handler)

    d.callback(data)
