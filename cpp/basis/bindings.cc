#include <fstream>
#include <iostream>
#include <map>

// 结构体解包出现继承的方式必须注意非静态成员只能出现
// 一个地方，要么基类，要么子类，不能混合
struct A { int x; int y; };
struct B : A {};

static B newB(void)
{
    B b;
    b.x = 12;
    b.y = 24;
    return b;
}

/**
 * structured bindings可以用于数组、结构体、std::pair、std::tuple等类型
 * 语法: auto [变量1, 变量2, …] = 数据;
 * 编译器需要支持C++17标准
 * 编译方法: g++ -std=c++17 bindings.cc
 */
int main(void)
{
    std::map<std::string, int> m;
    auto [iterator, success] = m.insert({"yy", 18});

    if (success) {
        std::cout << "插入数据成功" << std::endl;
    }

    for (auto const& [key, value]: m) {
        std::cout << "键: " << key << " 值: " << value << std::endl;
    }

    auto [x, y] = newB();
    std::cout << "x: " << x << " y: " << y << std::endl;
    return 0;
}

