from django.urls import path
from .views import PostList
from .views import PostDetail
from .views import UserList
from .views import UserDetail

urlpatterns = [
    path("<int:pk>/", PostDetail.as_view(), name="post_detail"),
    path("", PostList.as_view(), name="post_list"),
    path("users/<int:pk>/", UserDetail.as_view(), name="user_detail"),
    path("users/", UserList.as_view(), name="user_list"),
]
