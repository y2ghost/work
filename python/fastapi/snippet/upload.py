import shutil
from pathlib import Path
from fastapi import (
    APIRouter,
    File,
    HTTPException,
    UploadFile,
)
from fastapi.responses import FileResponse


router = APIRouter()


@router.post("/uploadfile")
async def upload_file(
    file: UploadFile = File(...),
):
    with open(
        f"uploads/{file.filename}", "wb"
    ) as buffer:
        shutil.copyfileobj(file.file, buffer)

    return {"filename": file.filename}


@router.get(
    "/downloadfile/{filename}",
    response_class=FileResponse,
)
async def download_file(filename: str):
    if not Path(f"uploads/{filename}").exists():
        raise HTTPException(
            status_code=404,
            detail=f"file {filename} not found",
        )

    return FileResponse(
        path=f"uploads/{filename}", filename=filename
    )
