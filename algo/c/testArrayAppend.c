#include <stdio.h>
#include <string.h>

// 线性表尾部插入值示例，也就是一维数组的操作
int main(int ac, char *av[])
{
    int array[] = {90, 70, 50, 80, 60, 85};
    int length = sizeof(array) / sizeof(array[0]);
    int tempArray[length + 1];
    memcpy(tempArray, array, sizeof(array));
    tempArray[length] = 75;
    int *scores = tempArray;

    for (int j = 0; j < length + 1; j++) {
        printf("%d,", scores[j]);
    }

    return 0;
}

