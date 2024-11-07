from django.shortcuts import render
from django.shortcuts import get_object_or_404
from bands.models import Musician
from django.core.paginator import Paginator

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

