#include <stdio.h>

static void sort(int array[], int length)
{
    for (int i=0; i<length; i++) {
        int insertValue = array[i];
        int insertIndex = i;

        for (int j=insertIndex-1; j>=0; j--) {
            if (insertValue < array[j]) {
                array[j+1] = array[j];
                insertIndex--;
            }
        }

        array[insertIndex] = insertValue;
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

