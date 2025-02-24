from content.models import SeekingAd
from django.contrib import admin
from django.utils.text import Truncator


@admin.register(SeekingAd)
class SeekingAdAdmin(admin.ModelAdmin):
    list_display = (
        "id",
        "date",
        "owner",
        "seeking",
        "show_ad",
    )

    def show_ad(self, obj):
        return Truncator(obj.content).words(5, truncate=" ...")

    show_ad.short_description = "Ad"
