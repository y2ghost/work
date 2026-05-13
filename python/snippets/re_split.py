import re

line = 'asdf fjdk; afed, fjek,asdf, foo'
items = re.split(r'[;,\s]\s*', line)
print(items)

