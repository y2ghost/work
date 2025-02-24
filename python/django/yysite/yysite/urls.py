from django.conf import settings
from django.conf.urls.static import static
from django.contrib import admin
from django.contrib.sitemaps.views import sitemap
from django.urls import path
from django.urls import include
from blog.sitemaps import PostSitemap

sitemaps = {
    "posts": PostSitemap,
}

urlpatterns = [
    path('admin/', admin.site.urls),
    path('blog/', include("blog.urls", namespace="blog")),
    path("sitemap.xml", sitemap,
        {"sitemaps": sitemaps},
        name="django.contrib.sitemaps.views.sitemap"
    ),
    path("account/", include("account.urls")),
    path("images/", include("images.urls", namespace="images")),
    path('__debug__/', include('debug_toolbar.urls')),
]

if settings.DEBUG:
    urlpatterns += static(
        settings.MEDIA_URL,
        document_root=settings.MEDIA_ROOT
    )

