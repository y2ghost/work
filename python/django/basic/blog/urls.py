from django.urls import path
from .views import BlogListView
from .views import BlogDetailView
from .views import BlogCreateView
from .views import BlogUpdateView
from .views import BlogDeleteView

urlpatterns = [
    path('post/<int:pk>/delete/', BlogDeleteView.as_view(), name='blogDelete'),
    path('post/<int:pk>/edit/', BlogUpdateView.as_view(), name='blogEdit'),
    path('post/new/', BlogCreateView.as_view(), name='blogNew'),
    path('post/<int:pk>/', BlogDetailView.as_view(), name='blogDetail'),
    path('', BlogListView.as_view(), name='blogHome'),
]
