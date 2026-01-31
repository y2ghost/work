#include <threads.h>
#include <stdio.h>

#define SIZE 5

static int thread_func(void *id)
{
    static _Thread_local int i;
    printf("From thread:[%d], Address of i (thread local): %p\n", *(int*)id, (void*)&i);
    return 0;
}

int main(void)
{
    thrd_t id[SIZE];
    int arr[SIZE] = {1,2,3,4,5};

    for (int i=0; i<SIZE; i++) {
        thrd_create(&id[i], thread_func, &arr[i]);
    }

    for (int j=0; j<SIZE; j++) {
        thrd_join(id[j], NULL);
    }

    return 0;
}
