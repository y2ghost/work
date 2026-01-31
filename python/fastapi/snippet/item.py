from fastapi import APIRouter


router = APIRouter()


@router.get("/items/{item_id}")
async def read_item(item_id: int):
    return {"itemId": item_id}

