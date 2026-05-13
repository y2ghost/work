from pydantic import BaseModel, Field


class Book(BaseModel):
    title: str = Field(
        ..., min_length=1, max_length=100
    )
    author: str = Field(
        ..., min_length=1, max_length=50
    )
    year: int = Field(..., gt=1900, lt=2100)


class Task(BaseModel):
    title: str
    description: str
    status: str


class TaskWithID(Task):
    id: int


class TaskV2(BaseModel):
    title: str
    description: str
    status: str
    priority: str | None = "low"


class TaskV2WithID(TaskV2):
    id: int


