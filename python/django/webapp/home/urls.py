from django.urls import path
from .views import credits
from .views import homePage
from .views import about
from .views import version
from .views import news

urlpatterns = [
    path("", homePage),
    path("credits/", credits, name="credits"),
    path("about/", about),
    path("version/", version),
    path("news/", news, name="news"),
    path("news2/", news, name="news2"),
]

