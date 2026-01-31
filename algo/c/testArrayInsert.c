#include <stdio.h>
#include <string.h>

// 线性表中间插入值示例，也就是一维数组的操作
void insert(int array[], int length, int value, int insertIndex)
{
    // insertIndex后面的元素往后挪
    for (int i=length-1; i>insertIndex; i--) {
        array[i] = array[i-1];
    }

    array[insertIndex] = value;
}

int main(int ac, char *av[])
{
    int array[] = {90, 70, 50, 80, 60, 85};
    int length = sizeof(array) / sizeof(array[0]);
    int tempArray[length + 1];
    memcpy(tempArray, array, sizeof(array));
    insert(tempArray, length+1, 75, 2);
    int *scores = tempArray;

    for (int j = 0; j < length+1; j++) {
        printf("%d,", scores[j]);
    }

    return 0;
}

