#include <stdio.h>
#include <getopt.h>
#include <string.h>

static const char *OUT_FILE = "out.txt";

static void usage(FILE *fp, const char *path)
{
    const char *basename = strrchr(path, '/');
    basename = basename ? basename + 1 : path;
    fprintf(fp, "用法: %s [OPTION]\n", basename);
    fprintf(fp, " -h, --help\t\t打印帮助说明并退出.\n");
    fprintf(fp, " -f, --file[=FILENAME]\t写入到文件(默认%s).\n", OUT_FILE);
    fprintf(fp, " -m, --msg=STRING\t输出的消息\n");
}

int main(int argc, char *argv[])
{
    char filename[256] = {0};
    char message[256] = "你好朋友";
    int help_flag = 0;
    struct option longopts[] = {
        { "help", no_argument, &help_flag, 1 },
        { "file", optional_argument, NULL, 'f' },
        { "msg", required_argument, NULL, 'm' },
        { 0 }
    };

    /**
     * 选项参数说明
     * 1.字符不更分号表示不需要参数
     * 2.字符后跟一个分号表示必需参数
     * 3.字符后跟两个分号表示可选参数
     */
    while (1) {
        int opt = getopt_long(argc, argv, "hf::m:", longopts, 0);
        if (-1 == opt) {
            break;
        }

        switch (opt) {
            case 'h':
                help_flag = 1;
                break;
            case 'f':
                snprintf(filename, sizeof(filename), "%s", optarg ? optarg : OUT_FILE);
                break;
            case 'm':
                snprintf(message, sizeof(message), "%s", optarg);
                break;
            case '?':
                usage(stderr, argv[0]);
                return 1;
            default:
                break;
        }
    }

    if (help_flag) {
        usage(stdout, argv[0]);
        return 0;
    }

    FILE *fp = NULL;
    if (filename[0]) {
        fp = fopen(filename, "w");
    } else {
        fp = stdout;
    }

    if (!fp) {
        fprintf(stderr, "无法打开文件!\n");
        return 1;
    }

    fprintf(fp, "%s\n", message);
    fclose(fp);
    return 0;
}

