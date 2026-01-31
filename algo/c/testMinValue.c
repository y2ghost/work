#include <stdio.h>

/*
 * 初始化minIndex = 0, i = 1
 * 如果arrays[minIndex]大于arrays[i]，则设置minIndex = i
 * i的值加一后继续上面的逻辑
 * 完成比较后最小值为arrays[minIndex]
 */
static int min(int arrays[], int length)
{
    int minIndex = 0;
    for (int i=1; i<length; i++) {
        if (arrays[minIndex] > arrays[i]) {
            minIndex = i;
        }
    }
    
    return arrays[minIndex];
}

int main(int ac, char *av[])
{
    int scores[] = {60, 80, 95, 50, 70};
    int length = sizeof(scores) / sizeof(scores[0]);
    int minValue = min(scores, length);
    printf("最小值 = %d\n", minValue);
    return 0;
}

