from fastapi import APIRouter, Depends, HTTPException
from pydantic import BaseModel, EmailStr, field_validator
from sqlalchemy.orm import Session
from database import SessionLocal, User


router = APIRouter()


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


class UserBody(BaseModel):
    name: str
    email: EmailStr
    age: int

    @field_validator("age")
    def validate_age(cls, value):
        if value < 18 or value > 100:
            raise ValueError( "Age must be between 18 and 100")
        return value


class UserResponse(BaseModel):
    id: str


@router.get("/users")
def read_users(db: Session = Depends(get_db)):
    users = db.query(User).all()
    return users


@router.post("/user")
def add_new_user(user: UserBody,
    db: Session = Depends(get_db)) -> UserResponse:
    new_user = User(name=user.name, email=user.email)
    db.add(new_user)
    db.commit()
    db.refresh(new_user)
    user = {
        "id": str(new_user["_id"]),
    }
    return user


@router.get("/user")
def get_user(user_id: int,
    db: Session = Depends(get_db)) -> UserResponse:
    db_user = (
        db.query(User)
        .filter(User.id == user_id)
        .first()
    )
    if db_user is None:
        raise HTTPException(
            status_code=404, detail="User not found"
        )
    user = {
        "id": str(db_user["_id"]),
    }
    return user


@router.post("/user/{user_id}")
def update_user(
    user_id: int,
    user: UserBody,
    db: Session = Depends(get_db),
):
    db_user = (
        db.query(User)
        .filter(User.id == user_id)
        .first()
    )
    if db_user is None:
        raise HTTPException(
            status_code=404, detail="User not found"
        )
    db_user.name = user.name
    db_user.email = user.email
    db.commit()
    db.refresh(db_user)
    return db_user


@router.delete("/user")
def delete_user(
    user_id: int, db: Session = Depends(get_db)
):
    db_user = (
        db.query(User)
        .filter(User.id == user_id)
        .first()
    )
    if db_user is None:
        raise HTTPException(
            status_code=404, detail="User not found"
        )
    db.delete(db_user)
    db.commit()
    return {"detail": "User deleted"}

