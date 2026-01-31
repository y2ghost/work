#include <stdio.h>

// 简单了解线性表的概念，就是一维数组顺序访问
int main(int ac, char *av[])
{
    int scores[] = {90, 70, 50, 80, 60, 85};
    int length = sizeof(scores) / sizeof(scores[0]);

    for (int i=0; i<length; i++) {
        printf("%d,", scores[i]);
    }

    return 0;
}

