#include <stdio.h>
#include <string.h>

// 线性表删除值示例，也就是一维数组的操作
void delete(int array[], int length, int deleteIndex)
{
    for (int i=deleteIndex+1; i<length; i++) {
        array[i-1] = array[i];
    }
}

int main(int ac, char *av[])
{
    int array[] = {90, 70, 50, 80, 60, 85};
    int length = sizeof(array) / sizeof(array[0]);
    int index = 0;
    scanf("%d", &index);
    delete(array, length, index);
    int *scores = array;

    // 删除后没有处理最后一个元素，此次打印元素个数减一
    for (int j = 0; j < length - 1; j++) {
        printf("%d,", scores[j]);
    }

    return 0;
}

