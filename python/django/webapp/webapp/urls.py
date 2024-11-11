from django.contrib import admin
from django.urls import path
from django.urls import include
from ninja import NinjaAPI
from home.api import router as home_router

api = NinjaAPI(version="1.0")
api.add_router("/api/", home_router)

urlpatterns = [
    path("admin/", admin.site.urls),
    path("accounts/", include("django.contrib.auth.urls")),
    path("", include("home.urls")),
    path("bands/", include("bands.urls")),
    path("content/", include("content.urls")),
    path("", api.urls),
]

