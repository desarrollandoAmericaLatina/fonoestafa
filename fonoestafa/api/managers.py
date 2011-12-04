from django.db.models import Manager
from fonoestafa.api.models import *

class HustlerManager(Manager):
    @staticmethod
    def is_a_hustler(number):
        try:
            return Hustler.objects.get(number = number, status = True)
        except:
            return None
