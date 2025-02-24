#include <iostream>
#include <chrono>

template<char FIRST, char... REST>
struct binary {
    static_assert('0' == FIRST || '1' == FIRST, "不合法的二进数字");
    enum { value = ((FIRST - '0') << sizeof...(REST)) + binary<REST...>::value };
};

template<>
struct binary<'0'>
{
    enum { value = 0 };
};

template<>
struct binary<'1'>
{
    enum { value = 1 };
};

// 可以定义用户字面值
// 字面量运算符的函数名以 operator"" 开头,后面紧跟用户定义后缀
template<char... LITERAL>
inline constexpr unsigned int operator "" _b() { return binary<LITERAL...>::value; }

template<char... LITERAL>
inline constexpr unsigned int operator "" _B() { return binary<LITERAL...>::value; }

// 自定义公里字面值函数
long double operator "" _km(long double val)
{
    return val * 1000.0;
}

long double operator "" _mi(long double val)
{
    return val * 1609.344;
}

int main(void)
{
    std::cout << 10101_B << ", " << 011011000111_b << "\n";
    // 时间相关的自定义字面值函数
    using namespace std::literals::chrono_literals;
    std::chrono::nanoseconds t1 = 600ns;
    std::chrono::microseconds t2 = 42us;
    std::chrono::milliseconds t3 = 51ms;
    std::chrono::seconds t4 = 61s;
    std::chrono::minutes t5 = 88min;
    auto t6 = 2 * 0.5h;
    auto total = t1 + t2 + t3 + t4 + t5 + t6;
    std::cout.precision(13);
    std::cout << total.count() << "纳秒\n";
    std::cout << std::chrono::duration_cast<std::chrono::hours>(total).count() << "小时\n";

    // 公里等字母值测试
    std::cout << "3km = " << 3.0_km << "m\n";
    std::cout << "3mi = " << 3.0_mi << "m\n";
    
    return 0;
}

