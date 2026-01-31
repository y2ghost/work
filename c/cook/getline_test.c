#include <stdlib.h>
#include <stdio.h>

static const char *TEST_FILE = "test/lines.txt";

int main(void)
{
    FILE *fp = fopen(TEST_FILE, "r");
    if (NULL == fp) {
        fprintf(stderr, "打开文件失败: %s\n", TEST_FILE);
        return EXIT_FAILURE;
    }

    char *buffer = NULL;
    size_t buffer_size = 0;
    int line_count = 0;

    while (1) {
        ssize_t line_size = getline(&buffer, &buffer_size, fp);
        if (line_size < 0) {
            break;
        }

        line_count++;
        printf("行[%06d]: 字节数=%06zd, 缓存大小=%06zu, 内容: %s", line_count,
            line_size, buffer_size, buffer);
    }

    free(buffer);
    buffer = NULL;
    fclose(fp);
    return EXIT_SUCCESS;
}

