from datetime import date, timedelta
from django.http import HttpResponse, JsonResponse
from django.shortcuts import render


def credits(request):
    content = "Nicky\nYour Name"

    return HttpResponse(content, content_type="text/plain")


def about(request):
    content = [
        "<!doctype html>",
        '<html lang="en">',
        "<head>",
        "  <title>YY About</title>",
        "</head>",
        "<body>",
        "  <h1>YY About</h1>",
        "  <p>",
        "    YY is a for musicians seeking musicians. Find your next ",
        "    band or band-mate. Find your next gig.",
        "  </p>",
        "</body>",
        "</html>",
    ]

    content = "\n".join(content)
    return HttpResponse(content, content_type="text/html")


def version(request):
    data = {
        "version": "0.0.1",
    }

    return JsonResponse(data)


def news(request):
    data = {
        "news": [
            "YY now has a news page!",
            "YY has its first web page",
        ],
    }

    return render(request, "news2.html", data)


def news_advanced(request):
    today = date.today()
    before1 = today - timedelta(days=1)
    before2 = today - timedelta(days=2)

    data = {
        "news": [
            (today, "Advanced news added! Even more exclamation points!!!"),
            (before1, "YY now has a news page!"),
            (before2, "YY has its first web page"),
        ],
    }

    return render(request, "adv_news.html", data)


def home(request):
    return render(request, "home.html")
