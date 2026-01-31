#include <stdio.h>

/*
 * 冒泡排序算法
 * 如果arrays[j]大于arrays[j+1]则交换它们
 * 重复执行比较直到排序完毕
 */
static void sort(int arrays[], int length)
{
    for (int i=0; i<length-1; i++) {
        for (int j=0; j<length-i-1; j++) {
            if (arrays[j] > arrays[j+1]) {
                int temp = arrays[j];
                arrays[j] = arrays[j+1];
                arrays[j+1] = temp;
            }
        }
    }
}

int main(int ac, char *av[])
{
    int scores[] = {90, 70, 50, 80, 60, 85};
    int length = sizeof(scores) / sizeof(scores[0]);
    sort(scores, length);

    for (int i=0; i<length; i++) {
        printf("%d,", scores[i]);
    }

    return 0;
}

