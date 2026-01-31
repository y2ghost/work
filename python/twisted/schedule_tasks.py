# coding=utf-8

from twisted.internet import task
from twisted.internet import reactor

def test1(s):
    print(f"this will run 3.5 seconds after it was scheduled: {s}")

# 测试延迟执行
reactor.callLater(3.5, test1, "hello test1")

def test2(s):
    return f"this will run 3.5 seconds after it was scheduled: {s}"

def test2_called(result):
    print(result)

# -------------------------

# 测试获取延迟执行的函数结果
d = task.deferLater(reactor, 3.5, test2, "hello test2")
d.addCallback(test2_called)

# ------------

# 测试循环执行
_loopTimes = 10
_failInTheEnd = False
_loopCounter = 0

def runEverySecond():
    global _loopCounter
    if _loopCounter < _loopTimes:
        _loopCounter += 1
        print('A new second has passed.')
        return

    if _failInTheEnd:
        raise Exception('Failure during loop execution.')

    loop.stop()
    return

def cbLoopDone(result):
    print("loop done.")
    reactor.stop()

def ebLoopFailed(failure):
    print(failure.getBriefTraceback())
    reactor.stop()

loop = task.LoopingCall(runEverySecond)
loopDeferred = loop.start(1.0)
loopDeferred.addCallback(cbLoopDone)
loopDeferred.addErrback(ebLoopFailed)

# ---------------

# 测试取消延迟任务
def test3():
    print("I will never run.")

cancelJob = reactor.callLater(10, test3)
cancelJob.cancel()

# ---------------

# 最后一起执行测试
reactor.run()

