import sys
from twisted.internet import reactor
from twisted.web.client import Agent
from twisted.web.http_headers import Headers

def versionToString(version):
    value_list = []
    for val in version:
        if isinstance(val, bytes):
            value_list.append(val.decode())
        else:
            value_list.append(str(val))

    return " ".join(value_list)

def printHeaders(response):
    print('HTTP version:', versionToString(response.version))
    print('Status code:', response.code)
    print('Status phrase:', response.phrase.decode())
    print('Response headers:')
    for header, value_list in response.headers.getAllRawHeaders():
        print(header.decode(), ",".join([value.decode() for value in value_list]))

def printError(failure):
    print(failure, file=sys.stderr)

def stop(result):
    reactor.stop()

if len(sys.argv) != 2:
    print("Usage: python print_metadata.py URL", file=sys.stderr)
    exit(1)

agent = Agent(reactor)
headers = Headers({
    'User-Agent': ['Twisted WebBot'],
    'Content-Type': ['text/x-greeting']})
d = agent.request(b'HEAD', sys.argv[1].encode(), headers=headers)
d.addCallbacks(printHeaders, printError)
d.addBoth(stop)
reactor.run()

