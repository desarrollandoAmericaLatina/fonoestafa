from django.conf.urls.defaults import *
from fonoestafa.api.views import home

# Uncomment the next two lines to enable the admin:
# from django.contrib import admin
# admin.autodiscover()

urlpatterns = patterns('',
    (r'^hustler/', include('fonoestafa.api.urls')),
    (r'^$', home)
)
