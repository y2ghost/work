用来学习twistd打包模块运行的例子
export PYTHONPATH=.
twistd [-n] -y echo_server.tac
ps aux | grep twistd    ## 确认是否运行

# 日志例子运行
twistd -ny logging.tac

