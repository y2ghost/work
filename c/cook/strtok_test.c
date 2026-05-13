#include <string.h>
#include <stdio.h>

int main(void)
{
    char src[] = "10.20,33.66,44.88";
    char *next = NULL;
    char *first = strtok_r(src, ",", &next);

    do {
        printf("[%s]\n", first);
        char *partNext = NULL;
        char *part = strtok_r(first, ".", &partNext);

        while (NULL != part) {
            printf("  [%s]\n", part);
            part = strtok_r(NULL, ".", &partNext);
        }
    } while (NULL != (first = strtok_r(NULL, ",", &next)));

    return 0;
}

