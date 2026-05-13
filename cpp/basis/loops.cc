#include <iostream>
#include <vector>

struct MyRange {
    float arr[3];
    const float* begin() const { return &arr[0]; }
    const float* end() const { return &arr[3]; }
    float* begin() { return &arr[0]; }
    float* end() { return &arr[3]; }
};

int main(void)
{
    // 下面的循环语法适合数组和迭代器类型
    std::vector<float> v = {0.4f, 12.5f, 16.234f};
    for (auto val: v) {
        std::cout << val << " ";
    }

    std::cout << std::endl;
    for (const float &val: v) {
        std::cout << val << " ";
    }

    std::cout << std::endl;
    std::vector<bool> bv(10);

    for (auto&& val: bv) {
        val = true;
    }

    MyRange rng = {{0.4f, 12.5f, 16.234f}};
    for (auto val: rng) {
        std::cout << val << " ";
    }

    std::cout << std::endl;
    return 0;
}

