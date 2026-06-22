from twisted.internet import reactor
from twisted.web.resource import Resource
from twisted.web.server import Site
import time

class ClockPage(Resource):
    isLeaf = True

    def render_GET(self, request):
        timeInfo = "The local time is %s" % (time.ctime(),)
        return timeInfo.encode()

resource = ClockPage()
factory = Site(resource)
reactor.listenTCP(8000, factory)
reactor.run()

