import re

text = 'Today is 11/27/2012. PyCon starts 3/13/2013.'
fix_text = re.sub(r'(\d+)/(\d+)/(\d+)', r'\3-\1-\2', text)
print(fix_text)

datepat = re.compile(r'(\d+)/(\d+)/(\d+)')
fix_text = datepat.sub(r'\3-\1-\2', text)
print(fix_text)

text = 'UPPER PYTHON, lower python, Mixed Python'
fix_text = re.findall('python', text, flags=re.IGNORECASE)
print(fix_text)
wordpat = re.compile(r'python', flags=re.IGNORECASE)
fix_text = wordpat.sub(r'snake', text)
print(fix_text)
