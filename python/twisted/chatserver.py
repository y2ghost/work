from twisted.internet.protocol import Factory
from twisted.protocols.basic import LineReceiver
from twisted.internet import reactor

class ChatProtocol(LineReceiver):
    def __init__(self, factory):
        self.factory = factory
        self.name = None
        self.state = "REGISTER"

    def connectionMade(self):
        self.sendMessage("What's your name?")

    def connectionLost(self, reason):
        if self.name in self.factory.users:
            del self.factory.users[self.name]
            self.broadcastMessage("%s has left the channel." % (self.name,))

    def lineReceived(self, data):
        line = data.decode(encoding='utf-8')
        if self.state == "REGISTER":
            self.handle_REGISTER(line)
        else:
            self.handle_CHAT(line)

    def handle_REGISTER(self, name):
        if name in self.factory.users:
            self.sendMessage("Name taken, please choose another.")
            return
        self.sendMessage("Welcome, %s!" % (name,))
        self.broadcastMessage("%s has joined the channel." % (name,))
        self.name = name
        self.factory.users[name] = self
        self.state = "CHAT"

    def handle_CHAT(self, message):
        message = "<%s> %s" % (self.name, message)
        self.broadcastMessage(message)

    def sendMessage(self, message):
        print("send Message: %s " % message)
        data = message.encode(encoding='utf-8')
        self.sendLine(data)

    def broadcastMessage(self, message):
        print("%s send Broad Message: %s " % (self.name, message))
        for name, protocol in self.factory.users.items():
            if protocol != self:
                protocol.sendLine(message.encode(encoding='utf-8'))

class ChatFactory(Factory):
    def __init__(self):
        self.users = {}

    def buildProtocol(self, addr):
        return ChatProtocol(self)

reactor.listenTCP(8000, ChatFactory())
reactor.run()

