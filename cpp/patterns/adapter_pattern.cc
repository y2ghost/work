#include <iostream>

class Rectangle {
public:
    virtual void draw() = 0;
};


class LegacyRectangle {
public:
    LegacyRectangle(int x1_, int y1_, int x2_, int y2_) {
        x1 = x1_;
        y1 = y1_;
        x2 = x2_;
        y2 = y2_;
        std::cout << "LegacyRectangle(x1,y1,x2,y2)\n";
    }

    void oldDraw() {
        std::cout << "LegacyRectangle oldDraw()\n";
    }

private:
    int x1;
    int y1;
    int x2;
    int y2;
};

class RectangleAdapter: public Rectangle, private LegacyRectangle {
public:
    RectangleAdapter(int x, int y, int w, int h):
        LegacyRectangle(x, y, x + w, y + h) {
        std::cout << "RectangleAdapter(x,y,x+w,x+h)\n";
    }

    void draw() {
        std::cout << "RectangleAdapter draw()\n";
        oldDraw();
    }
};

int main(void)
{
    int x = 20, y = 50, w = 300, h = 200;
    Rectangle *r = new RectangleAdapter(x,y,w,h);
    r->draw();
    return 0;
}

