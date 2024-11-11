from ninja import Router

router = Router()

@router.get("/hello")
def hello(request):
    return "hello yy"


@router.get("/version")
def version(request):
    data = {
        "version": "1.2.3",
    }
    return data

