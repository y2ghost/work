from django.urls import path
from .views import homePageView
from .views import HomePageView
from .views import AboutPageView

urlpatterns = [
    path('', HomePageView.as_view(), name='home'),
    path('home2', homePageView, name='home2'),
    path('about/', AboutPageView.as_view(), name='about'),
]

