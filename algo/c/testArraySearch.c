#include <stdio.h>
#include <string.h>

#define TRUE 1
#define FALSE 0

// 找到返回索引值，没找到返回-1
static int search(int array[], int length, int value)
{
    int index = -1;
    for (int i=0; i<length; i++) {
        if (value == array[i]) {
            index = i;
            break;
        }
    }

    return index;
}

int main(int ac, char *av[])
{
    int scores[] = { 90, 70, 50, 80, 60, 85 };
    int length = sizeof(scores) / sizeof(scores[0]);
    printf("请输入搜索值: \n");
    int value = 0;
    scanf("%d", &value);

    int index = search(scores, length, value);
    if (index > -1) {
        printf("找到值: %d 索引值: %d\n", value, index);
    } else {
        printf("未找到值: %d\n", value);
    }

    return 0;
}

