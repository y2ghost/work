from threading import local
from uuid import uuid4
_thread_locals = local()


def get_current_request():
    return getattr(_thread_locals, "request", None)


def get_current_user():
    request = get_current_request()
    if request:
        return getattr(request, "user", None)

def get_txid():
    return getattr(_thread_locals, "txid", None)

def get_current_user_id():
    user = get_current_user()
    if user and user.id:
        return user.id
    return 0


class PopulateLocalsThreadMiddleware:
    def __init__(self, get_response):
        self.get_response = get_response

    def __call__(self, request):
        print("custom middleware before next middleware/view")
        _thread_locals.request = request
        _thread_locals.txid = str(uuid4())
        response = self.get_response(request)
        _thread_locals.request = None
        _thread_locals.tx_id = None
        print("custom middleware after response is returned")
        return response

