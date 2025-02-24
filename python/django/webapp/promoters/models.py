from django.db import models


class Promoter(models.Model):
    common_name = models.CharField(max_length=25)
    full_name = models.CharField(max_length=50)
    famous_for = models.CharField(max_length=50)
    birth = models.DateField(blank=True, null=True)
    death = models.DateField(blank=True, null=True)
    address = models.TextField(blank=True, default="")
