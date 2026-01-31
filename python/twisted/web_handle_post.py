from twisted.internet import reactor
from twisted.web.resource import Resource
from twisted.web.server import Site
import cgi

class FormPage(Resource):
    isLeaf = True
    def render_GET(self, request):
        return b"""
<html>
 <body>
  <form method="POST">
   <input name="form-field" type="text" />
   <input type="submit" />
   </form>
   </body>
   </html>
"""

    def render_POST(self, request):
        value_list = request.args[b"form-field"]
        value = value_list[0].decode()
        rc = """
<html>
 <body>You submitted: %s</body>
 </html>
""" % (cgi.escape(value),)
        return rc.encode()

factory = Site(FormPage())
reactor.listenTCP(8000, factory)
reactor.run()

