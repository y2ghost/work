from django.urls import path
from django.urls import include
from .views import BookAPIView

urlpatterns = [
    path("books/", BookAPIView.as_view(), name="book_list"),
    path("todos/", include("todos.urls")),
    path("posts/", include("posts.urls")),
    path("dj-rest-auth/", include("dj_rest_auth.urls")),
    path("dj-rest-auth/registration/",
        include("dj_rest_auth.registration.urls")),
]
