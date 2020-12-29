from twisted.internet.defer import Deferred

def callback1(result):
    print("Callback 1 said:", result)
    return result

def callback2(result):
    print("Callback 2 said:", result)

def callback3(result):
    print("Callback 3 said:", result)
    raise Exception("Callback 3")

def errback1(failure):
    print("Errback 1 said:", failure)
    return failure

def errback2(failure):
    print("Errback 2 said:", failure)
    raise Exception("Errback 2")

def errback3(failure):
    print("Errback 3 said:", failure)
    return "Everything is fine now."

## 测试，执行流程如下
## callback3--异常--> errback3 --正常--> callback1
d = Deferred()
d.addCallback(callback3)
d.addCallbacks(callback2, errback3)
d.addCallbacks(callback1, errback2)
d.callback("Test")

