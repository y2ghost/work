from django.shortcuts import render
from django.shortcuts import redirect
from django.shortcuts import get_object_or_404
from django.core.mail import send_mail
from content.forms import CommentForm
from content.models import MusicianBandChoice
from content.models import SeekingAd
from django.contrib.auth.decorators import login_required
from content.forms import CommentForm, SeekingAdForm
from content.models import MusicianBandChoice, SeekingAd

def comment(request):
    if "GET" == request.method:
        form = CommentForm()
    else:
        form = CommentForm(request.POST)
        if form.is_valid():
            name = form.cleaned_data["name"]
            comment = form.cleaned_data["comment"]
            message = f"""
                Received comment from {name}\n\n
                {comment}
            """
            send_mail("Received comment", message,
                "admin@example.com", ["admin@example.com"],
                fail_silently=False)
            return redirect("comment_accepted")

    data = {
        "form": form,
    }

    return render(request, "comment.html", data)


def comment_accepted(request):
    data = {
        "content": """
            <h1>Comment Accepted</h1>
            <p>Thanks for submitting a comment to us</p>
        """
    }
    return render(request, "general.html", data)


def list_ads(request):
    data = {
        "seeking_musician": SeekingAd.objects.filter(
            seeking=MusicianBandChoice.MUSICIAN),
        "seeking_band": SeekingAd.objects.filter(
            seeking=MusicianBandChoice.BAND),
    }
    return render(request, "list_ads.html", data)


@login_required
def seeking_ad(request, ad_id=0):
    if "GET" == request.method:
        if 0 == ad_id:
            form = SeekingAdForm()
        else:
            ad = get_object_or_404(SeekingAd, id=ad_id,
                owner=request.user)
            form = SeekingAdForm(instance=ad)
    else:
        if 0 == ad_id:
            form = SeekingAdForm(request.POST)
        else:
            ad = get_object_or_404(SeekingAd, id=ad_id,
                owner=request.user)
            form = SeekingAdForm(request.POST,
                instance=ad)
        if form.is_valid():
            ad = form.save(commit=False)
            ad.owner = request.user
            ad.save()
            return redirect("list_ads")
    data = {
        "form": form,
    }
    return render(request, "seeking_ad.html", data)


