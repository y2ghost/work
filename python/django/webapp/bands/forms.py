from bands.models import Musician, Room, Venue
from django import forms

VenueForm = forms.modelform_factory(
    Venue, fields=["name", "description", "picture"]
)

MusicianForm = forms.modelform_factory(
    Musician,
    fields=["first_name", "last_name", "birth", "description", "picture"],
)

RoomForm = forms.modelform_factory(
    Room,
    fields=[
        "name",
    ],
)
