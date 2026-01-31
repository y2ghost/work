#include <stdio.h>

/*
 * 选择排序算法
 */
static void sort(int arrays[], int length)
{
    for (int i=0; i<length-1; i++) {
        int minIndex = i;
        int minValue = arrays[minIndex];

        for (int j=i; j<length-1; j++) {
            if (minValue > arrays[j+1]) {
                minIndex = j + 1;
                minValue = arrays[minIndex];
            }
        }

        if (i != minIndex) {
            int temp = arrays[i];
            arrays[i] = minValue;
            arrays[minIndex] = temp;
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

