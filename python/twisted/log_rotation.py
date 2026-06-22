from twisted.python import log
from twisted.python import logfile

f = logfile.LogFile("test.log", "/tmp", rotateLength=100)
log.startLogging(f)

for i in range(10):
    log.msg("this is a test of the logfile: {}".format(i))

f.rotate()
log.msg("goodbye")

