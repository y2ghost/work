from django.shortcuts import render
from django.shortcuts import get_object_or_404
from django.shortcuts import redirect
from movies.models import Movie
from .utils import calculate_cart_total
from .models import Order
from .models import Item
from django.contrib.auth.decorators import login_required

def index(request):
    cart_total = 0
    movies_in_cart = []
    cart = request.session.get("cart", {})
    movie_ids = list(cart.keys())
    if (movie_ids != []):
        movies_in_cart = Movie.objects.filter(id__in=movie_ids)
        cart_total = calculate_cart_total(cart, movies_in_cart)

    data = {
        "template_data": {
        "title": "Cart",
        "movies_in_cart": movies_in_cart,
        "cart_total": cart_total,
        }
    }
    return render(request, "cart/index.html", data)

def add(request, id):
    get_object_or_404(Movie, id=id)
    cart = request.session.get("cart", {})
    cart[id] = request.POST["quantity"]
    request.session["cart"] = cart
    return redirect("cart.index")

def clear(request):
    request.session["cart"] = {}
    return redirect("cart.index")


@login_required
def purchase(request):
    cart = request.session.get("cart", {})
    movie_ids = list(cart.keys())

    if (movie_ids == []):
        return redirect("cart.index")

    movies_in_cart = Movie.objects.filter(id__in=movie_ids)
    cart_total = calculate_cart_total(cart, movies_in_cart)

    order = Order()
    order.user = request.user
    order.total = cart_total
    order.save()

    for movie in movies_in_cart:
        item = Item()
        item.movie = movie
        item.price = movie.price
        item.order = order
        item.quantity = cart[str(movie.id)]
        item.save()

    request.session["cart"] = {}
    data = {
        "template_data": {
            "title": "Purchase confirmation",
            "order_id": order.id,
        }
    }
    return render(request, "cart/purchase.html", data)

