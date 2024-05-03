from django.contrib import admin
from django.urls import path
from django.urls import include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('accounts/', include('django.contrib.auth.urls')),
    path('accounts/', include('accounts.urls')),
    path('api/', include('api.urls')),
    path('todos/', include('todos.urls')),
    path('auth/', include('rest_framework.urls')),
    path('books/', include('books.urls')),
    path('', include('pages.urls')),
]
