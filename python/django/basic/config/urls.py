from django.contrib import admin
from django.urls import path
from django.urls import include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('pages.urls')),
    path('posts/', include('posts.urls')),
    path('blog/', include('blog.urls')),
    # 下面的相同的URL匹配方式属于按序匹配，匹配上了处理，否则转给下一个处理
    path('accounts/', include('django.contrib.auth.urls')),
    path('accounts/', include('accounts.urls')),
]
