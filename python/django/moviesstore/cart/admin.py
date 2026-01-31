from django.contrib import admin
from .models import Order
from .models import Item

admin.site.register(Order)
admin.site.register(Item)
