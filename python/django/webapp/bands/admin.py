from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as BaseUserAdmin
from django.contrib.auth.models import User
from bands.models import Musician
from bands.models import Band
from bands.models import Venue
from bands.models import Room
from bands.models import UserProfile
from datetime import datetime
from datetime import date
from django.utils.html import format_html
from django.urls import reverse

class DecadeListFilter(admin.SimpleListFilter):
    title = "decade born"
    parameter_name = "decade"

    def lookups(self, request, model_admin):
        result = []
        this_year = datetime.today().year
        this_decade = (this_year // 10) * 10
        start = this_decade - 10
        for year in range(start, start - 100, -10):
            result.append((str(year), f"{year}-{year+9}"))
        return result

    def queryset(self, request, queryset):
        start = self.value()
        if start is None:
            return queryset

        start = int(start)
        result = queryset.filter(
            birth__gte=date(start,1, 1),
            birth__lte=date(start+9, 12, 31),
        )
        return result


@admin.register(Musician)
class MusicianAdmin(admin.ModelAdmin):
    # 定义显示的属性名称列表，支持属性名和方法名
    list_display = ("id", "last_name", "show_weekday", "show_bands")
    # 定义搜索字段
    search_fields = ("last_name", "first_name")
    list_filter = (DecadeListFilter,)

    def show_weekday(self, obj):
        return obj.birth.strftime("%A")
    show_weekday.short_description = "Birth Weekday"

    def show_bands(self,obj):
        bands = obj.band_set.all()
        if len(bands) == 0:
            return format_html("<i>None</i>")
        param ="?id__in=" + ",".join([str(b.id) for b in bands])
        url = reverse("admin:bands_band_changelist") + param
        return format_html('<a href="{}">Bands</a>', url)
    show_bands.short_description = "Bands"


@admin.register(Band)
class BandAdmin(admin.ModelAdmin):
    pass


class UserProfileInline(admin.StackedInline):
    model = UserProfile
    can_delete = False


class UserAdmin(BaseUserAdmin):
    inlines = [UserProfileInline]


admin.site.unregister(User)
admin.site.register(User, UserAdmin)

