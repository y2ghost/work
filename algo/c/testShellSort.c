#include <stdio.h>

static void swap(int array[], int a, int b)
{
    array[a] = array[a] + array[b];
    array[b] = array[a] - array[b];
    array[a] = array[a] - array[b];
}

static void shellSort(int array[], int length)
{
    for (int gap=length/2; gap > 0; gap=gap/2) {
        for (int i=gap; i<length; i++) {
            int j = i;
            while (j-gap>=0 && array[j]<array[j-gap]) {
                swap(array, j, j-gap);
                j = j - gap;
            }
        }
    }
}

int main(int ac, char *av[]) {
    int scores[] = { 9, 6, 5, 8, 0, 7, 4, 3, 1, 2 };
    int length = sizeof(scores) / sizeof(scores[0]);
    shellSort(scores, length);

    for (int i=0; i<length; i++) {
        printf("%d,", scores[i]);
    }
    
    return 0;
}
