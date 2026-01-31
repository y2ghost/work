from twisted.internet import reactor
from twisted.internet.defer import Deferred
from twisted.internet.protocol import Protocol
from twisted.web.client import Agent
from twisted.web.http_headers import Headers
from pprint import pformat
import sys

class HttpPrinter(Protocol):
    def __init__(self, finished):
        self.finished = finished
        self.remaining = 1024 * 10

    def dataReceived(self, data):
        if self.remaining:
            display = data[:self.remaining]
            print('Some data received:')
            print(display)
            self.remaining -= len(display)

    def connectionLost(self, reason):
        print('Finished receiving body:', reason.getErrorMessage())
        self.finished.callback(None)

def printPage(response):
    print('Response version:', response.version)
    print('Response code:', response.code)
    print('Response phrase:', response.phrase)
    print('Response headers:')
    print(pformat(list(response.headers.getAllRawHeaders())))
    finished = Deferred()
    response.deliverBody(HttpPrinter(finished))
    return finished

def printError(failure):
    print(failure, file=sys.stderr)

def stop(result):
    reactor.stop()

if len(sys.argv) != 2:
    print("Usage: python print_resource.py <URL>", file=sys.stderr)
    exit(1)

agent = Agent(reactor)
d = agent.request(
    b'GET',
    sys.argv[1].encode(),
    Headers({'User-Agent': ['YY Client']}),
    None)

d.addCallbacks(printPage, printError)
d.addBoth(stop)
reactor.run()

