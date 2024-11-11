from django import forms
from content.models import SeekingAd

class CommentForm(forms.Form):
    name = forms.CharField()
    widget = forms.Textarea(attrs={"rows": "6", "cols": "50"})
    comment = forms.CharField(widget=widget)

class SeekingAdForm(forms.ModelForm):
    class Meta:
        model = SeekingAd
        fields = ["seeking", "musician", "band", "content",]

    def __init(self, *args, **kwargs):
        super().__init__(*args, **kwargs)
        self.fields["seeking"].label = "I am seeking a"
        self.fields["musician"].help_text = \
            "Fill in if you are a musician seeking a band"
        self.fields["band"].help_text = \
            "Fill in if you are a band seeking a musician"


