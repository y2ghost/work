#include <stdio.h>

/*
 * 初始化maxIndex = 0, i = 1
 * 如果arrays[maxIndex]小于arrays[i]，则设置maxIndex = i
 * i的值加一后继续上面的逻辑
 * 完成比较后最大值为arrays[maxIndex]
 */
static int max(int arrays[], int length)
{
    int maxIndex = 0;
    for (int i=1; i<length; i++) {
        if (arrays[maxIndex] < arrays[i]) {
            maxIndex = i;
        }
    }
    
    return arrays[maxIndex];
}

int main(int ac, char *av[])
{
    int scores[] = {60, 50, 95, 80, 70};
    int length = sizeof(scores) / sizeof(scores[0]);
    int maxValue = max(scores, length);
    printf("最大值 = %d\n", maxValue);
    return 0;
}

