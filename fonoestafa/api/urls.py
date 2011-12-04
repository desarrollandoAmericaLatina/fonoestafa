from django.conf.urls.defaults import *
from fonoestafa.api.views import *

urlpatterns = patterns('',
    url(r'^create', create, name='hustler.create'),
    url(r'^ask', ask, name='hustler.ask'),
)
