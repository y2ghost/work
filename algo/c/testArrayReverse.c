#include <stdio.h>

static void reverse(int arrays[], int length)
{
    int middle = length / 2;
    for (int i=0; i<=middle; i++) {
        int temp = arrays[i];
        arrays[i] = arrays[length - i - 1];
        arrays[length - i - 1] = temp;
    }
}

int main(int ac, char *av[])
{
    int scores[] = { 50, 60, 70, 80, 90 };
    int length = sizeof(scores) / sizeof(scores[0]);
    reverse(scores, length);

    for (int i=0; i<length; i++) {
        printf("%d,", scores[i]);
    }

    return 0;
}
