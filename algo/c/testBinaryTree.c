#include <stdio.h>
#include <stdlib.h>

/*
 * left 节点的值小于根节点的值
 * right节点的值大于根节点的值
 */
typedef struct Node {
    int data;
    struct Node *left;
    struct Node *right;
} Node;

static Node *root_ = NULL;

static Node *createNode(int data)
{
    Node *node = malloc(sizeof(node));
    node->data = data;
    node->left = NULL;
    node->right = NULL;
    return node;
}

// 中序遍历: 左边节点 -> 根节点 -> 右边节点
static void inOrder(Node *node)
{
    if (NULL == node) {
        return;
    }

    inOrder(node->left);
    printf("%d,", node->data);
    inOrder(node->right);
}

// 前序遍历: 根节点 -> 左边节点 -> 右边节点
static void preOrder(Node *node)
{
    if (NULL == node) {
        return;
    }

    printf("%d,", node->data);
    preOrder(node->left);
    preOrder(node->right);
}

// 后序遍历: 左节点 -> 右节点 -> 根节点
static void postOrder(Node *node)
{
    if (NULL == node) {
        return;
    }

    postOrder(node->left);
    postOrder(node->right);
    printf("%d,", node->data);
}

static void insert(Node *node, int data)
{
    if (NULL == root_) {
        root_ = malloc(sizeof(root_));
        root_->data = data;
        root_->left = NULL;
        root_->right = NULL;
        return;
    }

    int diff = data - node->data;
    if (diff < 0) {
        if (NULL == node->left) {
            node->left = createNode(data);
        } else {
            insert(node->left, data);
        }
    } else if (diff > 0) {
        if (NULL == node->right) {
            node->right = createNode(data);
        } else {
            insert(node->right, data);
        }
    }
}

static Node *getMinValue(Node *node)
{
    if (NULL == node) {
        return NULL;
    }

    if (NULL == node->left) {
        return node;
    }

    return getMinValue(node->left);
}

static Node *getMaxValue(Node *node)
{
    if (NULL == node) {
        return NULL;
    }

    if (NULL == node->right) {
        return node;
    }

    return getMaxValue(node->right);
}

static Node *removeNode(Node *node, int data)
{
    if (NULL == node) {
        return NULL;
    }

    int diff = data - node->data;
    if (diff > 0) {
        node->right = removeNode(node->right, data);
    } else if (diff < 0) {
        node->left = removeNode(node->left, data);
    } else if (NULL != node->left && NULL != node->right) {
        node->data = getMinValue(node->right)->data;
        node->right = removeNode(node->right, node->data);
    } else {
        node = (NULL != node->left) ? node->left : node->right;
    }

    return node;
}


static void freeNode(Node *node)
{
    if (NULL == node) {
        return;
    }

    freeNode(node->left);
    freeNode(node->right);
    free(node);
}

int main(int ac, char *av[])
{
    // 构造数据
    insert(root_, 60);
    insert(root_, 40);
    insert(root_, 20);
    insert(root_, 10);
    insert(root_, 30);
    insert(root_, 50);
    insert(root_, 80);
    insert(root_, 70);
    insert(root_, 90);

    // 遍历示例
    printf("中序遍历二叉搜索树\n\n");
    inOrder(root_);
    printf("\n前序遍历二叉搜索树\n\n");
    preOrder(root_);
    printf("\n后序遍历二叉搜索树\n\n");
    postOrder(root_);

    // 最小值和最大值
    Node *node = getMinValue(root_);
    printf("\n最小值: %d\n", node->data);
    node = getMaxValue(root_);
    printf("\n最大值: %d\n", node->data);

    // 删除节点
    removeNode(root_, 40);
    inOrder(root_);

    // 清理资源
    freeNode(root_);
    return 0;
}
