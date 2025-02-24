from django.shortcuts import render
from django.shortcuts import redirect
from django.shortcuts import get_object_or_404
from django.contrib.auth.decorators import login_required
from .models import Movie
from .models import Review


def index(request):
    search_term = request.GET.get("search")
    if search_term:
        movies = Movie.objects.filter(name__icontains=search_term)
    else:
        movies = Movie.objects.all()
    data = {
        "template_data": {
            "title": "Movies",
            "movies": movies,
        }
    }
    return render(request, "movies/index.html", data)

def show(request, id):
    movie = Movie.objects.get(id=id)
    reviews = Review.objects.filter(movie=movie)
    data = {
        "template_data": {
            "title": movie.name,
            "movie": movie,
            "reviews": reviews,
        }
    }
    return render(request, "movies/show.html", data);


@login_required
def create_review(request, id):
    if request.method == "POST" and request.POST["comment"] != "":
        movie = Movie.objects.get(id=id)
        review = Review()
        review.comment = request.POST["comment"]
        review.movie = movie
        review.user = request.user
        review.save()
    return redirect("movies.show", id=id)


@login_required
def edit_review(request, id, review_id):
    review = get_object_or_404(Review, id=review_id)
    if request.user != review.user:
        return redirect("movies.show", id=id)

    if request.method == "GET":
        template_data = {
            "title": "Edit Review",
            "review": review,
        }
        data = {
            "template_data": template_data,
        }
        return render(request, "movies/edit_review.html", data)
    elif request.method == "POST" and request.POST["comment"] != "":
        review = Review.objects.get(id=review_id)
        review.comment = request.POST["comment"]
        review.save()
    return redirect("movies.show", id=id)


@login_required
def delete_review(request, id, review_id):
    review = get_object_or_404(Review, id=review_id, user=request.user)
    review.delete()
    return redirect("movies.show", id=id)

