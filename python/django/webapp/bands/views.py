from django.shortcuts import render
from django.shortcuts import get_object_or_404
from django.shortcuts import redirect
from bands.models import Musician
from bands.models import Band
from bands.models import Venue
from django.core.paginator import Paginator
from django.contrib.auth.decorators import login_required
from django.http import Http404
from bands.forms import MusicianForm
from bands.forms import VenueForm

def musician(request, musician_id):
    entity = get_object_or_404(Musician, id = musician_id)
    data = {
        "musician": entity,
    }
    return render(request, "musician.html", data)


def _get_page_size(request):
    page_size = int(request.GET.get("page_size", 10))
    if page_size < 1:
        page_size = 1
    elif page_size > 50:
        page_size = 50
    return page_size


def _get_page_num(request, paginator):
    page_num = int(request.GET.get("page", 1))
    if page_num < 1:
        page_num = 1
    elif page_num > paginator.num_pages:
        page_num = paginator.num_pages
    return page_num


def musicians(request):
    entities = Musician.objects.all().order_by("last_name")
    page_size = _get_page_size(request)
    paginator = Paginator(entities, page_size)
    page_num = _get_page_num(request, paginator)
    page = paginator.page(page_num)
    data = {
        "musicians": page.object_list,
        "page": page,
    }
    return render(request, "musicians.html", data)


def band(request, band_id):
    data = {
        "band": get_object_or_404(Band, id=band_id),
    }
    return render(request, "band.html", data)


def bands(request):
    all_bands = Band.objects.all().order_by("name")
    items_per_page = _get_page_size(request)
    paginator = Paginator(all_bands, items_per_page)
    page_num = _get_page_num(request, paginator)
    page = paginator.page(page_num)
    data = {
        "bands": page.object_list,
        "page": page,
    }
    return render(request, "bands.html", data)


def venues(request):
    all_venues = Venue.objects.all().order_by("name")
    items_per_page = _get_page_size(request)
    paginator = Paginator(all_venues, items_per_page)
    page_num = _get_page_num(request, paginator)
    page = paginator.page(page_num)
    data = {
        "venues": page.object_list,
        "page": page,
    }
    return render(request, "venues.html", data)


@login_required
def restricted_page(request):
    data = {
        "title": "Restricted Page",
        "content": "<h1>You are logged in</h1>",
    }
    return render(request, "general.html", data)


@login_required
def musician_restricted(request, musician_id):
    musician = get_object_or_404(Musician, id=musician_id)
    profile = request.user.userprofile
    allowed = False

    if profile.musician_profiles.filter(id=musician_id).exists():
        allowed = True
    else:
        musician_profiles = set(profile.musician_profiles.all())
        for band in musician.band_set.all():
            band_musicians = set(band.musicians.all())
            if musician_profiles.intersection(band_musicians):
                allowed = True
                break

    if not allowed:
        raise Http404("permission denied")

    content = f"""
        <h1>Musician Page: {musician.last_name}</h1>
    """
    data = {
        "title": "Musician Restricted",
        "content": content,
    }
    return render(request, "general.html", data)


@login_required
def edit_venue(request, venue_id=0):
    if venue_id != 0:
        venue = get_object_or_404(Venue, id=venue_id)
        if not request.user.userprofile.venues_controlled.filter(
            id=venue_id
        ).exists():
            raise Http404("Can only edit controlled venues")

    if request.method == "GET":
        if venue_id == 0:
            form = VenueForm()
        else:
            form = VenueForm(instance=venue)

    else:  # POST
        if venue_id == 0:
            venue = Venue.objects.create()

        form = VenueForm(request.POST, request.FILES, instance=venue)

        if form.is_valid():
            venue = form.save()

            # Add the venue to the user's profile
            request.user.userprofile.venues_controlled.add(venue)
            return redirect("venues")

    # Was a GET, or Form was not valid
    data = {
        "form": form,
    }

    return render(request, "edit_venue.html", data)

