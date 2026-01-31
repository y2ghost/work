import html

s = 'Elements are written as "<tag>text</tag>".'
print(s)
print(html.escape(s))
escaped = html.escape(s, quote=False)
print(escaped)
s = html.unescape(escaped)
print(s)
