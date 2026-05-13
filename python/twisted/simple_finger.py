# coding=utf8

from twisted.internet import protocol
from twisted.internet import reactor
from twisted.internet import endpoints
from twisted.internet import defer
from twisted.internet import utils
from twisted.protocols import basic

class FingerProtocol(basic.LineReceiver):
    def lineReceived(self, user):
        def onError(err):
            return b"Internal error in server"

        def writeResponse(message):
            self.sendLine(message)
            self.transport.loseConnection()

        d = self.factory.getUser(user)
        # 先处理异常情况
        d.addErrback(onError)
        d.addCallback(writeResponse)

class FingerFactory(protocol.ServerFactory):
    protocol = FingerProtocol
    NO_USER = bytes("没有发现用户", encoding="utf8")

    def getUser(self, user):
        return utils.getProcessOutput(b"finger", [user])

fingerEndpoint = endpoints.serverFromString(reactor, "tcp:1079")
fingerEndpoint.listen(FingerFactory())
reactor.run()

