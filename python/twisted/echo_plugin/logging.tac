from twisted.application.service import Application
from twisted.python.log import ILogObserver, msg
from twisted.python.util import untilConcludes
from twisted.internet.task import LoopingCall

logfile = open("logging.log", "a")

def log(eventDict):
    untilConcludes(logfile.write, "Got a log! {}\n".format(eventDict))
    untilConcludes(logfile.flush)

def logSomething():
    msg("A log message")

LoopingCall(logSomething).start(1)
application = Application("twistd-logging")
application.setComponent(ILogObserver, log)

