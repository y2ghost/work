from django.urls import path
from django.urls import include
from .views import BookAPIView

urlpatterns = [
    path("books/", BookAPIView.as_view(), name="book_list"),
    path("todos/", include("todos.urls")),
]
