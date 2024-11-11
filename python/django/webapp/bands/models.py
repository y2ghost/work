from django.contrib.auth.models import User
from django.db import models
from django.db.models.signals import post_save
from django.dispatch import receiver

class Musician(models.Model):
    first_name = models.CharField(max_length=50)
    last_name = models.CharField(max_length=50)
    birth = models.DateField()
    description = models.TextField(blank=True)
    picture = models.ImageField(blank=True, null=True)

    class Meta:
        ordering = ["last_name", "first_name"]

    def __str__(self):
        return f"Musician(id={self.id}, last_name={self.last_name})"


class Venue(models.Model):
    name = models.CharField(max_length=20)
    description = models.TextField(blank=True)
    picture = models.ImageField(blank=True, null=True)

    class Meta:
        ordering = ["name",]

    def __str__(self):
        return f"Venue(id={self.id}, name={self.name})"


class Room(models.Model):
    name = models.CharField(max_length=20)
    venue = models.ForeignKey(Venue, on_delete=models.CASCADE)

    class Meta:
        ordering = ["name",]

    def __str__(self):
        return f"Room(id={self.id}, name={self.name})"


class Band(models.Model):
    name = models.CharField(max_length=20)
    musicians = models.ManyToManyField(Musician)

    class Meta:
        ordering = ["name",]

    def __str__(self):
        return f"Band(id={self.id}, name={self.name})"


class UserProfile(models.Model):
    user = models.OneToOneField(User, on_delete=models.CASCADE)
    musician_profiles = models.ManyToManyField(Musician, blank=True)
    venues_controlled = models.ManyToManyField(Venue, blank=True)


@receiver(post_save, sender=User)
def user_post_save(sender, **kwargs):
    if kwargs["created"] and not kwargs["raw"]:
        user = kwargs["instance"]
        try:
            UserProfile.objects.get(user=user)
        except UserProfile.DoesNotExist:
            UserProfile.objects.create(user=user)
            print("great, automatically created userprofile")


