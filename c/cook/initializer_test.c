#include <stdio.h>

// 指定初始化示例
struct Date {
    int year;
    int month;
    int day;
};

struct number_union {
    enum { NU_INT, NU_DOUBLE } type;
    union {
        int nu_int;
        double nu_double;
    } nu;
};

int main(void)
{
    int array[] = { [4] = 29, [5] = 31, [17] = 101, [18] = 103, [19] = 107, [20] = 109 };
    struct Date testDay = { .day = 11, .month = 11, .year = 2023 };
    struct number_union nu1 = {.type = NU_INT, .nu = {.nu_int = 3 }};
    struct number_union nu2 = {.type = NU_DOUBLE, .nu = {.nu_double = 3.14 }};
    printf("计算结果: %lf\n", array[4] + testDay.day + nu1.nu.nu_int + nu2.nu.nu_double);
    return 0;
}

