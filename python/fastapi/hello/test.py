import requests
import httpx
import datetime
from fastapi.encoders import jsonable_encoder

def request_get(url, params={}):
    r = requests.get(url, params=params)
    print(r.json())

def httpx_get(url, params={}):
    r = httpx.get(url, params=params)
    print(r.json())

def request_post(url, params={}):
    r = requests.post(url, json=params)
    print(r.json())

def httpx_post(url, params={}):
    r = httpx.post(url, json=params)
    print(r.json())

def test_encoder(data):
    import json
    out = jsonable_encoder(data)
    assert out
    json_out = json.dumps(out)
    assert json_out
    print(f"json_out: {json_out}")

def test_auth():
    r = requests.get("http://localhost:8000/me",
        auth=("me", "secret"))
    print(r.json())


if "__main__" == __name__:
    # 基本测试
    request_get("http://localhost:8000/hi");
    httpx_get("http://localhost:8000/hi");
    # 路径参数
    request_get("http://localhost:8000/hipath/zhangsan");
    httpx_get("http://localhost:8000/hipath/lsi");
    # 请求参数
    params = {"who": "Dog"}
    request_get("http://localhost:8000/hiparam", params);
    httpx_get("http://localhost:8000/hiparam", params);
    params = {"who": "girl"}
    request_post("http://localhost:8000/hibody", params)
    httpx_post("http://localhost:8000/hibody", params)
    test_encoder(params)
    test_auth()

