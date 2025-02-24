from django.shortcuts import render


def index(request):
    data = {
        "template_data": {
            "title": "Movies Store",
        }
    }
    return render(request, "home/index.html", data)


def about(request):
    data = {
        "template_data": {
            "title": "About",
        }
    }
    return render(request, "home/about.html", data)

