#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// 队列先进先出
typedef struct Node {
    char data[32];
    struct Node *prev;
    struct Node *next;
} Node;

static Node *head = NULL;
static Node *tail = NULL;
static size_t DATA_LEN = sizeof(((Node*)0)->data) - 1;
static int size = 0;

// 队尾追加元素
static void offer(char *data)
{
    Node *newNode = malloc(sizeof(Node));
    strncpy(newNode->data, data, DATA_LEN);

    if (NULL == head) {
        newNode->prev = NULL;
        newNode->next = NULL;
        head = newNode;
        tail = head;
    } else {
        newNode->prev = NULL;
        newNode->next = tail;
        tail->prev = newNode;
        tail = newNode;
    }

    size++;
}

// 队首弹出元素
static Node* pop()
{
    Node *p = head;
    if (NULL == p) {
        return NULL;
    }

    head = head->prev;
    p->next = NULL;
    p->prev = NULL;
    size--;
    return p;
}

static void output()
{
    printf("队首 ");
    while (1) {
        Node *node = pop();
        if (NULL == node) {
            break;
        }

        printf("%s <- ", node->data);
        free(node);
    }

    printf("队尾\n\n");
}

static void freeHead()
{
    Node *p = head;
    while (NULL != p) {
        Node *temp = p;
        p = p->next;
        free(temp);
    }
}

int main(int ac, char *av[]) {
    offer("A");
    offer("B");
    offer("C");
    offer("D");
    output();
    freeHead();
    return 0;
}

