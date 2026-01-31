from tcp_factory import DeviceFactory
from tcp_factory import apFactory
from twisted.application import service
from twisted.application import internet
from twisted.logger import LogLevelFilterPredicate
from twisted.logger import FilteringLogObserver
from twisted.logger import LogLevel
from twisted.logger import ILogObserver
from twisted.logger import textFileLogObserver
from twisted.python.logfile import DailyLogFile

# 定义日志输出文件的LogObserver对象
logFile = DailyLogFile("tcp_server.log", '/tmp')
levelPredicate =  LogLevelFilterPredicate(LogLevel.info)
logObserver = FilteringLogObserver(textFileLogObserver(logFile),
    predicates=[levelPredicate])

# 创建应用并应用自定义的LogObserver对象
application = service.Application("TcpCommunication")
application.setComponent(ILogObserver, logObserver)
deviceService = internet.TCPServer(6666, DeviceFactory())
deviceService.setServiceParent(application)
stationService = internet.TCPServer(8888, apFactory())
stationService.setServiceParent(application)

