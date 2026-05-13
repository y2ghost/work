from twisted.internet import reactor, protocol

class QuoteProtocol(protocol.Protocol):
    ## 常见的用法，使用factory持久化存储
    def __init__(self, factory):
        self.factory = factory

    def connectionMade(self):
        self.factory.numConnections += 1

    def dataReceived(self, data):
        print("number of active connections: %d" % self.factory.numConnections)
        print("recevied: %s, Sending: %s" % (data, self.getQuote()))
        self.transport.write(self.getQuote())
        self.updateQuote(data)

    def connectionLost(self, reason):
        self.factory.numConnections -= 1

    def getQuote(self):
        return self.factory.quote

    def updateQuote(self, quote):
        self.factory.quote = quote

class QuoteFactory(protocol.Factory):
    numConnections = 0

    def __init__(self, quote=None):
        self.quote = quote or b"hello yy"

    def buildProtocol(self, addr):
        return QuoteProtocol(self)

reactor.listenTCP(8000, QuoteFactory())
reactor.run()

