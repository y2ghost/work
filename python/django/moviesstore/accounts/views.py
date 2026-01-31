from django.shortcuts import render
from django.contrib.auth import login as auth_login
from django.contrib.auth import authenticate
from django.contrib.auth import logout as auth_logout
from .forms import CustomUserCreationForm
from .forms import CustomErrorList
from django.shortcuts import redirect
from django.contrib.auth.decorators import login_required
from django.contrib.auth.models import User

@login_required
def logout(request):
    auth_logout(request)
    return redirect("home.index")

def login(request):
    template_data = {
        "title": "Login",
    }
    data = {
        "template_data": template_data,
    }
    if request.method == "GET":
        return render(request, "accounts/login.html", data)
    elif request.method == "POST":
        user = authenticate(request, username = request.POST["username"], password = request.POST["password"])
        if user is None:
            template_data["error"] = "The username or password is incorrect."
            return render(request, "accounts/login.html", data)
        else:
            auth_login(request, user)
            return redirect("home.index")


def signup(request):
    template_data = {
        "title": "Sign Up",
    }
    data = {
        "template_data": template_data,
    }
    if request.method == "GET":
        template_data["form"] = CustomUserCreationForm()
        return render(request, "accounts/signup.html", data)
    elif request.method == "POST":
        form = CustomUserCreationForm(request.POST, error_class=CustomErrorList)
        if form.is_valid():
            form.save()
            return redirect("accounts.login")
        else:
            template_data["form"] = form
            return render(request, "accounts/signup.html", data)


@login_required
def orders(request):
    data = {
        "template_data": {
            "title": "Orders",
            "orders": request.user.order_set.all(),
        }
    }
    return render(request, "accounts/orders.html", data)

