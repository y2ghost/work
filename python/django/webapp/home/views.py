from django.http import HttpResponse
from django.http import JsonResponse
from django.shortcuts import render

def credits(request):
    content = "Nicky\nYour Name"
    return HttpResponse(content, content_type="text/palin")

def homePage(request):
    content = "Hello YY"
    return HttpResponse(content, content_type="text/palin")

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
        "    this is a about page for study",
        "  </p>",
        "</body>",
        "</html>",
    ]

    content = "\n".join(content)
    return HttpResponse(content, content_type="text/html")

def version(request):
    data = {
        "version": "1.2.3",
    }

    return JsonResponse(data)

def news_internal(request, template):
    data = {
        "news": [
            "y1 now has a news page!",
            "y2 has its first web page",
        ],
    }
    return render(request, template, data)

def news(request):
    return news_internal(request, "news.html")

def news2(request):
    return news_internal(request, "news2.html")

