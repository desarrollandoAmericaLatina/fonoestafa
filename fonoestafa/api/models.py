from django.db import models

class Hustler(models.Model):
    number = models.CharField(max_length=100, blank=False)
    status = models.SmallIntegerField(blank=True, default=0)
    created_at = models.DateTimeField(editable=False,auto_now_add=True)

    def __unicode__(self):
        return  '%d - %s' % (self.id, self.number)

class Event(models.Model):
    hustler = models.ForeignKey('Hustler', blank=False)
    comments = models.TextField(blank=True)
    created_at = models.DateTimeField(editable=False,auto_now_add=True)

    def __unicode__(self):
        return  '%d - %s' % (self.id, self.hustler)
