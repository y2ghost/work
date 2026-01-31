from twisted.internet import reactor, defer

class HeadlineRetriever(object):
    def processHeadline(self, headline):
        if len(headline) > 50:
            err = "The headline ``%s'' is too long!" % (headline,)
            self.d.errback(ValueError(err))
        else:
            self.d.callback(headline)

    def _toHTML(self, result):
        return "<h1>%s</h1>" % (result,)

    def getHeadline(self, input):
        self.d = defer.Deferred()
        reactor.callLater(1, self.processHeadline, input)
        self.d.addCallback(self._toHTML)
        return self.d

def printData(result):
    print(result)
    reactor.stop()

def printError(failure):
    print(failure)
    reactor.stop()

h = HeadlineRetriever()
d = h.getHeadline("Breaking News: Twisted Takes us to the Moon!")
# 可以测试error callback
# d = h.getHeadline("1234567890"*6)
d.addCallbacks(printData, printError)

reactor.run()
