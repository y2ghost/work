import asyncio
import time
import uvicorn
from contextlib import contextmanager
from multiprocessing import Process
from httpx import AsyncClient
from fastapi.testclient import TestClient
from main import app


def test_read_book_by_id():
    client = TestClient(app)
    response = client.get("/books/9999")
    assert response.status_code == 200
    assert response.json() == {
        "bookId": 9999,
        "title": "The Great Gatsby",
        "author": "F. Scott Fitzgerald",
    }


def run_server():
    uvicorn.run(app, port=8000, log_level="error")


@contextmanager
def run_server_in_process():
    p = Process(target=run_server)
    p.start()
    time.sleep(2)
    print("Server is running in a separate process")
    yield
    p.terminate()


async def make_requests(n: int, path: str):
    async with AsyncClient(
        base_url="http://localhost:8000"
    ) as client:
        tasks = (
            client.get(path, timeout=float("inf"))
            for _ in range(n)
        )
        await asyncio.gather(*tasks)


async def main(n: int = 10):
    with run_server_in_process():
        begin = time.time()
        await make_requests(n, "/sync")
        end = time.time()
        print(
            f"Time taken to make {n} requests "
            f"to sync endpoint: {end - begin} seconds"
        )

        begin = time.time()
        await make_requests(n, "/async")
        end = time.time()
        print(
            f"Time taken to make {n} requests "
            f"to async endpoint: {end - begin} seconds"
        )


if __name__ == "__main__":
    test_read_book_by_id()
    asyncio.run(main(n=100))

