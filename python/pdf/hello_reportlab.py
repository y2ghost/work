from reportlab.pdfgen import canvas
from reportlab.lib.pagesizes import A4

my_canvas = canvas.Canvas("hello.pdf", pagesize=A4)
# coordinate system:
#   y
#   |
#   |   page
#   |
#   |
#   0-------x
my_canvas.drawString(50, 780, "study reportlab!")
my_canvas.showPage()
my_canvas.save()

