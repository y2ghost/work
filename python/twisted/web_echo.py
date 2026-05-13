from twisted.protocols import basic
from twisted.internet import protocol, reactor

class HTTPEchoProtocol(basic.LineReceiver):
    def __init__(self):
        self.lines = []

    def lineReceived(self, line):
        self.lines.append(line.decode())
        if not line:
            self.sendResponse()

    def sendResponse(self):
        self.sendLine(b"HTTP/1.1 200 OK")
        self.sendLine(b"")
        responseBody = "You said:\r\n\r\n" + "\r\n".join(self.lines)
        data = responseBody.encode()
        self.transport.write(data)
        self.transport.loseConnection()

class HTTPEchoFactory(protocol.ServerFactory):
    def buildProtocol(self, addr):
        return HTTPEchoProtocol()

reactor.listenTCP(8000, HTTPEchoFactory())
reactor.run()

