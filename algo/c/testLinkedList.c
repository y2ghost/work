#include <stdio.h>
#include <stdlib.h>
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
    head = (Node*)malloc(sizeof(Node));
    strncpy(head->data, "老大", DATA_LEN);
    head->next = NULL;

    Node *zhangsan = malloc(sizeof(Node));
    strncpy(zhangsan->data, "张三", DATA_LEN);
    zhangsan->next = NULL;
    head->next = zhangsan;

    Node *lisi = malloc(sizeof(Node));
    strncpy(lisi->data, "李四", DATA_LEN);
    lisi->next = NULL;
    zhangsan->next = lisi;

    Node *wangwu = malloc(sizeof(Node));
    strncpy(wangwu->data, "王五", DATA_LEN);
    wangwu->next = NULL;
    lisi->next = wangwu;
    tail = wangwu;
}

static void appendNode(char *data)
{
    Node *newNode = malloc(sizeof(Node));
    strncpy(newNode->data, data, DATA_LEN);
    newNode->next = NULL;
    tail->next = newNode;
    tail = newNode;
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
    while (NULL != p) {
        printf("%s -> ", p->data);
        p = p->next;
    }

    printf("结束\n\n");
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
    init();
    output(head);
    printf("尾部插入新节点\n");
    appendNode("尾巴");
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

