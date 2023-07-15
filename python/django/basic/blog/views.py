from django.views.generic import ListView
from django.views.generic import DetailView
from django.views.generic.edit import CreateView
from django.views.generic.edit import UpdateView
from django.views.generic.edit import DeleteView
from django.urls import reverse_lazy
from .models import Post


class BlogListView(ListView):
    model = Post
    template_name = 'blog_home.html'
    context_object_name = 'all_blog_list'


class BlogDetailView(DetailView):
    model = Post
    template_name = 'blog_detail.html'
    context_object_name = 'blog'


class BlogCreateView(CreateView):
    model = Post
    template_name = 'blog_new.html'
    context_object_name = 'blog'
    fields = ['title', 'author', 'body']


class BlogUpdateView(UpdateView):
    model = Post
    template_name = 'blog_edit.html'
    context_object_name = 'blog'
    fields = ['title', 'body']


class BlogDeleteView(DeleteView):
    model = Post
    template_name = 'blog_delete.html'
    context_object_name = 'blog'
    success_url = reverse_lazy('blogHome')
