from django.http import HttpResponse
from django.views.generic import TemplateView

""" harde code view example"""
def homePageView(request):
    return HttpResponse('Hard Code Home Page')

""" template file view example"""
class HomePageView(TemplateView):
    template_name = 'home.html'

class AboutPageView(TemplateView):
    template_name = 'about.html'

