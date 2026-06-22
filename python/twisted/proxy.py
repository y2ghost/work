import time
from twisted.internet import reactor
from twisted.protocols import portforward
from twisted.internet import threads
from twisted.internet.threads import deferToThread

def processingOperation(data):
    time.sleep(1)
    return data

def server_dataReceived(self, data):
    data = processingOperation(data)
    portforward.Proxy.dataReceived(self, data)

def client_dataReceived(self, data):
    portforward.Proxy.dataReceived(self, data)

def render_response(self, request):
    d = deferToThread(client_dataReceived, request)

portforward.ProxyServer.dataReceived = server_dataReceived
portforward.ProxyClient.dataReceived = client_dataReceived
reactor.listenTCP(6600, portforward.ProxyFactory('127.0.0.1', 6666))
reactor.run()

