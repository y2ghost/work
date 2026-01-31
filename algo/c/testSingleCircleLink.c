#include <stdio.h>
#include<stdlib.h>
#include <string.h>

typedef struct Node {
    char data[32];
    struct Node *next;
} Node;

static Node *head = NULL;
static Node *tail = NULL;
static size_t DATA_LEN = sizeof(((Node*)0)->data) - 1;

static void init()
{
    head = malloc(sizeof(Node));
    strncpy(head->data, "A", DATA_LEN);
    head->next = NULL;

    Node *nodeB = malloc(sizeof(Node));
    strncpy(nodeB->data, "B", DATA_LEN);
    nodeB->next = NULL;
    head->next = nodeB;

    Node *nodeC = malloc(sizeof(Node));
    strncpy(nodeC->data, "C", DATA_LEN);
    nodeC->next = NULL;
    nodeB->next = nodeC;

    tail = malloc(sizeof(Node));
    strncpy(tail->data, "D", DATA_LEN);
    tail->next = head;
    nodeC->next = tail;
}

static void insertNode(int position, char *data)
{
    int i = 0;
    Node *p = head;

    while (NULL != p->next && i < position - 1) {
        p = p->next;
        i++;
    }

    Node *newNode = malloc(sizeof(Node));
    strncpy(newNode->data, data, DATA_LEN);
    newNode->next = p->next;
    p->next = newNode;
}

static void removeNode(int position)
{
    int i = 0;
    Node *p = head;

    while (NULL != p->next && i < position - 1) {
        p = p->next;
        i++;
    }

    Node *tempNode = p->next;
    p->next = p->next->next;
    tempNode->next = NULL;
    free(tempNode);
}

static void output(Node *node)
{
    Node *p = node;
    do {
        printf("%s -> ", p->data);
        p = p->next;
    } while (p != head);
    printf("%s \n\n", p->data);
}

static void freeHead()
{
    Node *p = head;
    do {
        Node *temp = p;
        p = p->next;
        free(temp);
    } while (p != head);
}

int main(int ac, char *av[])
{
    init();
    output(head);
    printf("中间插入新节点\n");
    insertNode(2, "插队");
    output(head);
    printf("删除节点\n");
    removeNode(2);
    output(head);
    freeHead();
    return 0;
}
