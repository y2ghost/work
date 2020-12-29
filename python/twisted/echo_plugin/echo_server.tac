from twisted.application import internet, service
from twisted.logger import ILogObserver, textFileLogObserver
from twisted.python.logfile import DailyLogFile
from twisted.python import log
from echo import EchoFactory

application = service.Application("echo")
logfile = DailyLogFile("echo.log", "/tmp")
application.setComponent(ILogObserver, textFileLogObserver(logfile))
log.msg("good luck for me")
echoService = internet.TCPServer(8000, EchoFactory())
echoService.setServiceParent(application)

