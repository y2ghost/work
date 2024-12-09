import asyncio
import time
import json
import logging
from fastapi import (
    FastAPI,
    Depends,
    HTTPException,
    Request,
    status,
)
from asyncio import gather
from contextlib import asynccontextmanager
from fastapi.exceptions import RequestValidationError
from fastapi.responses import PlainTextResponse
from pydantic import BaseModel
from starlette.responses import JSONResponse
from item import router as itemRouter
from user import router as userRouter
from upload import router as uploadRouter
from task import router as taskRouter
from models import Book
from conn import (
    ping_redis_server,
    redis_client,
)


@asynccontextmanager
async def lifespan(app: FastAPI):
    await gather(
        ping_redis_server(),
    )
    yield


app = FastAPI(lifespan=lifespan)
app.include_router(itemRouter)
app.include_router(userRouter)
app.include_router(uploadRouter)
app.include_router(taskRouter)
logger = logging.getLogger("main")


@app.get("/")
async def read_root():
    return {"Hello": "World"}


@app.get("/books/{book_id}")
async def read_book(book_id: int):
    return {
        "bookId": book_id,
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
    }


@app.get("/authors/{author_id}")
async def read_author(author_id: int):
    return {
        "authorId": author_id,
        "name": "Ernest Hemingway",
    }


@app.get("/books")
async def read_books(year: int = None):
    if year:
        return {
            "year": year,
            "books": ["Book 1", "Book 2"],
        }
    return {"books": ["All Books"]}


@app.post("/books")
async def create_book(book: Book):
    return book


class BookResponse(BaseModel):
    title: str
    author: str


@app.get("/allbooks")
async def read_all_books() -> list[BookResponse]:
    return [
        {
            "id": 1,
            "title": "1984",
            "author": "George Orwell",
        },
        {
            "id": 2,
            "title": "The Great Gatsby",
            "author": "F. Scott Fitzgerald",
        },
    ]


@app.exception_handler(HTTPException)
async def http_exception_handler(request, exc):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "message": "Oops! Something went wrong"
        },
    )


@app.get("/error_endpoint")
async def raise_exception():
    raise HTTPException(status_code=400)


@app.exception_handler(RequestValidationError)
async def validation_exception_handler(
    request: Request, exc: RequestValidationError
):
    return PlainTextResponse(
        "This is a plain text response:"
        f" \n{json.dumps(exc.errors(), indent=2)}",
        status_code=status.HTTP_400_BAD_REQUEST,
    )


@app.get("/sync")
def read_sync():
    time.sleep(2)
    return {
        "message": "Synchrounouns blocking endpoint"
    }


@app.get("/async")
async def read_async():
    await asyncio.sleep(2)
    return {
        "message": "Asynchronous non-blocking endpoint"
    }


def get_redis_client():
    return redis_client


@app.get("/cache")
async def cache_me(who: str, redis_client=Depends(get_redis_client)):
    cache_key = f"cache:{who}"
    cached_data = await redis_client.get(cache_key)
    if cached_data:
        logger.info(f"Returning cached data for {who}")
        return json.loads(cached_data)
    logger.info(f"set cache data for {who}")
    who_data = {
        "who": who,
        "data": f"{who} got cached",
    }
    await redis_client.set(
        cache_key, json.dumps(who_data), ex=3600
    )
    return who_data

