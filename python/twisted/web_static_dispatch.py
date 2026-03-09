from twisted.internet import reactor
from twisted.web.server import Site
from twisted.web.static import File

root = File('./static')
root.putChild(b"doc", File("./doc"))
factory = Site(root)
reactor.listenTCP(8000, factory)
reactor.run()

