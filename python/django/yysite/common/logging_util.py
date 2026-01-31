import json
import logging

from common.localthread_middleware import get_current_user_id
from common.localthread_middleware import get_txid

def log_event(event_name, log_data, logging_module="django_default", level="INFO"):
    """
    :param event_name: 事件名称
    :param log_data: 事件数据
    :param logging_module: 自定义模块
    :param level: 日志级别
    """
    logger = logging.getLogger(logging_module)

    try:
        msg = {"ev": event_name, "data": log_data, "txid": get_txid()}
        user_id = get_current_user_id()
        if user_id:
            msg["uid"] = user_id
        logger.log(msg=json.dumps(msg), level=getattr(logging, level))
    except Exception as e:
        print('Error')
        return

