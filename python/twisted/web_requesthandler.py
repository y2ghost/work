from twisted.internet import reactor
from twisted.web import http

class MyRequestHandler(http.Request):
    resources = {
        '/': b'<h1>Home</h1>Home page',
        '/about': b'<h1>About</h1>All about me',
        }

    def process(self):
        self.setHeader('Content-Type', 'text/html')
        path = self.path.decode()
        if path in self.resources:
            self.write(self.resources[path])
        else:
            self.setResponseCode(http.NOT_FOUND)
            self.write(b"<h1>Not Found</h1>Sorry, no such resource.")
        self.finish()

class MyHTTP(http.HTTPChannel):
    requestFactory = MyRequestHandler

class MyHTTPFactory(http.HTTPFactory):
    def buildProtocol(self, addr):
        return MyHTTP()

if __name__ == "__main__":
    reactor.listenTCP(8000, MyHTTPFactory())
    reactor.run()

