from reportlab.pdfgen import canvas

my_canvas = canvas.Canvas("hello.pdf")
my_canvas.drawString(100, 750, "welcome to reportlab!")
my_canvas.save()
