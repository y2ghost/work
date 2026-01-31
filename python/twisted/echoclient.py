from twisted.internet import reactor, protocol

class EchoClient(protocol.Protocol):
    def connectionMade(self):
        data = "hello 杨勇".encode(encoding='utf-8')
        self.transport.write(data)
    
    def dataReceived(self, data):
        print("Server said:", data.decode(encoding='utf-8'))
        self.transport.loseConnection()

class EchoFactory(protocol.ClientFactory):
    def buildProtocol(self, addr):
        return EchoClient()

    def clientConnectionFailed(self, connector, reason):
        print("connection failed")
        reactor.stop()

    def clientConnectionLost(self, connector, reason):
        print("connection lost")
        reactor.stop()

reactor.connectTCP("localhost", 8000, EchoFactory())
reactor.run()

