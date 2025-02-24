#include <iostream>
#include <map>

unsigned get_fib_tail(unsigned n, unsigned prev = 0, unsigned curr = 1)
{
    if (0 == n) {
        return prev;
    }

    if (1 == n) {
        return curr;
    }

    return get_fib_tail(n-1, curr, prev + curr);
}

unsigned get_fib_mem(unsigned n, std::map<unsigned, unsigned>& values)
{
    if (n <= 1) {
        return n;
    }

    auto iter = values.find(n);
    if (values.end() == iter) {
        return values[n] = get_fib_mem(n-1, values) + get_fib_mem(n-2, values);
    } else {
        return iter->second;
    }
}

int main(void)
{
    // 存储的值放在外部方便自由添加或是删除
    std::map<unsigned, unsigned> values;
    std::cout << get_fib_tail(18) << std::endl;
    std::cout << get_fib_mem(18, values) << std::endl;
    return 0;
}

