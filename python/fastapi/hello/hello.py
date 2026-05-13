from fastapi import FastAPI
from fastapi import Body
from fastapi import Header
from fastapi import Response
from fastapi import Depends
from typing import Annotated
from fastapi.security import HTTPBasic, HTTPBasicCredentials


app = FastAPI()
basic = HTTPBasic()

@app.get("/hi")
def greet():
    return "hello yy"

@app.get("/hipath/{who}")
def greet_path(who):
    return f"hello path {who}"

@app.get("/hiparam")
def greet_param(who):
    return f"hello param {who}"

@app.post("/hibody")
def greet_body(who:str = Body(embed=True)):
    return f"hello body {who}"

@app.post("/hihead")
def greet_header(who:str = Header()):
    return f"hello head {who}"

@app.post("/agent")
def greet_header(user_agent:str = Header()):
    return f"hello agent {user_agent}"

# 指定响应的HTTP状态码
@app.get("/happy", status_code=201)
def happy():
    return ":)"

@app.get("/header/{name}/{value}")
def header(name:str, value: str, response: Response):
    response.headers[name] = value
    return "get header"

async def common_parameters(q: str | None = None, skip: int = 0, limit: int = 100):
    return {"q": q, "skip": skip, "limit": limit}

@app.get("/user")
async def get_user(user: Annotated[dict, Depends(common_parameters)]) -> dict:
    return user

@app.get("/me")
def get_me(
    creds: HTTPBasicCredentials = Depends(basic)):
    return {"username": creds.username, "password": creds.password}

if "__main__" == __name__:
    import uvicorn
    uvicorn.run("hello:app", reload=True, host="0.0.0.0")

