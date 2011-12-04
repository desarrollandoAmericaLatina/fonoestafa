from django import forms

class DenounceForm(forms.Form):
    number   = forms.CharField(max_length=100, required=True)
    comments = forms.CharField(required=False)
    user = forms.CharField(required=False)
    password = forms.CharField(required=False)

class AskForm(forms.Form):
    number   = forms.CharField(max_length=100, required=True)
