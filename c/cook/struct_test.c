#include <stdio.h>
#include <stdlib.h>

struct ex1 {
    size_t foo;
    int flex[];
};

struct ex2_header {
    int foo;
    char bar;
};

struct ex2 {
    struct ex2_header hdr;
    int flex[];
};

struct ex3 {
    int foo;
    char bar;
    int flex[];
};

int main(void)
{
    printf("%zu,%zu\n", sizeof(size_t), sizeof(struct ex1));
    printf("%zu,%zu\n", sizeof(struct ex2_header), sizeof(struct ex2));
    printf("%zu,%zu\n", sizeof(int) + sizeof(char), sizeof(struct ex3));
    struct ex1 *pe1 = malloc(sizeof(*pe1) + 2 * sizeof(pe1->flex[0]));
    struct ex2 *pe2 = malloc(sizeof(struct ex2) + sizeof(int[4]));
    struct ex3 *pe3 = malloc(5 * (sizeof(*pe3) + sizeof(int[3])));
    pe1->flex[0] = 3;
    pe3[0].flex[0] = pe1->flex[0];
    free(pe1);
    free(pe2);
    free(pe3);
    return 0;
}
