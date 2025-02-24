from django.core.mail import send_mail
from django.core.paginator import EmptyPage
from django.core.paginator import Paginator
from django.shortcuts import render
from django.shortcuts import get_object_or_404
from django.http import Http404
from django.views.generic import ListView
from django.views.decorators.http import require_POST
from taggit.models import Tag
from django.db.models import Count
from django.contrib.postgres.search import SearchVector
from django.contrib.postgres.search import SearchQuery
from django.contrib.postgres.search import SearchRank
from .models import Post
from .forms import EmailPostForm
from .forms import CommentForm
from .forms import SearchForm


def _get_page_num(request, paginator):
    try:
        page_num = int(request.GET.get("page", 1))
    except ValueError:
        page_num = 1

    if page_num < 1:
        page_num = 1
    elif page_num > paginator.num_pages:
        page_num = paginator.num_pages
    return page_num


def post_list(request, tag_slug=None):
    post_list = Post.published.all()
    tag = None
    if tag_slug:
        tag = get_object_or_404(Tag, slug=tag_slug)
        post_list = post_list.filter(tags__in=[tag])

    paginator = Paginator(post_list, 3)
    page_number = _get_page_num(request, paginator)
    posts = paginator.page(page_number)
    data = {
        "posts": posts,
        "tag": tag,
    }
    return render(
        request,
        "blog/post/list.html",
        data
    )


# 示例获取post的一种方式
def get_post(id):
    try:
        post = Post.published.get(id=id)
        data = {"post": post}
    except Post.DoesNotExist:
        raise Http404("No Post found.")
    return data


def post_detail(request, year, month, day, post):
    post = get_object_or_404(
        Post,
        status=Post.Status.PUBLISHED,
        slug=post,
        publish__year=year,
        publish__month=month,
        publish__day=day
    )
    comments = post.comments.filter(active=True)
    form = CommentForm()
    post_tags_ids = post.tags.values_list("id", flat=True)
    similar_posts = Post.published.filter(
        tags__in=post_tags_ids
    ).exclude(id=post.id)
    similar_posts = similar_posts.annotate(
        same_tags=Count("tags")
    ).order_by("-same_tags", "-publish")[:4]
    data = {
        "post": post,
        "comments": comments,
        "form": form,
        "similar_posts": similar_posts,
    }
    return render(
        request,
        "blog/post/detail.html",
        data
    )


class PostListView(ListView):
    queryset = Post.published.all()
    context_object_name = "posts"
    paginate_by = 3
    template_name = "blog/post/list.html"


def post_share(request, post_id):
    post = get_object_or_404(
        Post,
        id=post_id,
        status=Post.Status.PUBLISHED
    )
    sent = False

    if request.method == "POST":
        form = EmailPostForm(request.POST)
        if form.is_valid():
            cd = form.cleaned_data
            post_url = request.build_absolute_uri(
                post.get_absolute_url()
            )
            subject = (
                f"{cd['name']} ({cd['email']})"
                f"recommends you read {post.title}"
            )
            message = (
                f"Read {post.title} at {post_url}\n\n"
                f"{cd['name']}\'s comments: {cd['comments']}"
            )
            send_mail(
                subject=subject,
                message=message,
                from_email=None,
                recipient_list=[cd["to"]]
            )
            sent = True
    else:
        form = EmailPostForm()
    data = {
        "post": post,
        "form": form,
        "sent": sent,
    }
    return render(
        request,
        "blog/post/share.html",
        data
    )


@require_POST
def post_comment(request, post_id):
    post = get_object_or_404(
        Post,
        id=post_id,
        status=Post.Status.PUBLISHED
    )
    comment = None
    form = CommentForm(data=request.POST)

    if form.is_valid():
        comment = form.save(commit=False)
        comment.post = post
        comment.save()

    data = {
        "post": post,
        "form": form,
        "comment": comment,
    }
    return render(
        request,
        "blog/post/comment.html",
        data
    )


def post_search(request):
    form = SearchForm()
    query = None
    results = []

    if "query" in request.GET:
        form = SearchForm(request.GET)
        if form.is_valid():
            query = form.cleaned_data["query"]
            search_vector = SearchVector("title", "body")
            search_query = SearchQuery(query)
            results = (
                Post.published.annotate(
                    search=search_vector,
                    rank=SearchRank(search_vector, search_query)
                )
                .filter(search=query)
                .order_by("-rank")
            )

    data = {
        "form": form,
        "query": query,
        "results": results,
    }
    return render(
        request,
        "blog/post/search.html",
        data
    )


