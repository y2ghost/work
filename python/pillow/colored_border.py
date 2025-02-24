from PIL import Image, ImageOps


def add_border(input_image, output_image, border, color=0):
    img = Image.open(input_image)

    if isinstance(border, int) or isinstance(
            border, tuple):
        bimg = ImageOps.expand(img,
                               border=border,
                               fill=color)
    else:
        msg = 'Border is not an integer or tuple!'
        raise RuntimeError(msg)

    bimg.save(output_image)


if __name__ == '__main__':
    in_img = 'butterfly_grey.jpg'

    add_border(in_img,
               output_image='out-butterfly_border_red.jpg',
               border=100,
               color='indianred')
