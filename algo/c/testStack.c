#include <stdio.h>
#include <stdlib.h>
#include <string.h>

// 堆栈后进先出
typedef struct Node {
    char data[32];
    struct Node *prev;
    struct Node *next;
} Node;

static Node *top = NULL;
static size_t DATA_LEN = sizeof(((Node*)0)->data) - 1;
static int size = 0;

// 栈顶压入节点
static void push(char *data)
{
    Node *newNode = malloc(sizeof(Node));
    strncpy(newNode->data, data, DATA_LEN);

    if (NULL == top) {
        newNode->prev = NULL;
        newNode->next = NULL;
        top = newNode;
    } else {
        newNode->prev = NULL;
        newNode->next = top;
        top = newNode;
    }

    size++;
}

// 栈顶弹出节点
static Node* pop()
{
    if (NULL == top) {
        return NULL;
    }

    Node *p = top;
    top = top->next;
    p->next = NULL;
    size--;
    return p;
}

static void output()
{
    printf("栈顶 ");
    while (1) {
        Node *node = pop();
        if (NULL == node) {
            break;
        }

        printf("%s <- ", node->data);
        free(node);
    }

    printf("栈底\n\n");
}

static void freeHead()
{
    Node *p = top;
    while (NULL != p) {
        Node *temp = p;
        p = p->next;
        free(temp);
    }
}

int main(int ac, char *av[]) {
    push("A");
    push("B");
    push("C");
    push("D");
    output();
    freeHead();
    return 0;
}

