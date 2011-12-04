from django.http import HttpResponse
from django.shortcuts import render_to_response
from fonoestafa.api.models import *
from fonoestafa.api.managers import *
from fonoestafa.api.forms import *

def create(request):

    form = DenounceForm(request.GET)
    if form.is_valid():
        number = form.cleaned_data['number']
        user = form.cleaned_data['user']
        password = form.cleaned_data['password']
        comments = form.cleaned_data['comments']
        hustler, is_new = Hustler.objects.get_or_create( number = number, defaults={'status': 1})
        event = Event.objects.create(hustler = hustler, comments = comments)
        return HttpResponse('ok')
    else:
        return HttpResponse('error')

def ask(request):
    form = AskForm(request.GET)
    if form.is_valid():
        hustler = HustlerManager.is_a_hustler(form.cleaned_data['number'])
        if hustler:
            return HttpResponse('si;%s' % str(hustler.created_at).split()[0])
        return HttpResponse('no')
    else:
        return HttpResponse('invalid')

def home(request):
    return render_to_response('base.html')
