import logging
from redis import asyncio as aioredis


logger = logging.getLogger("connection")
redis_client = aioredis.from_url("redis://localhost")


async def ping_redis_server():
    try:
        await redis_client.ping()
        logger.info("Connected to Redis")
    except Exception as e:
        logger.error(f"Error connecting to Redis: {e}")
        raise e

