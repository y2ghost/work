#include "common.h"
#include <libssh/libssh.h>

int main(int ac, char *av[])
{
    printf("libssh version: %s\n", ssh_version(0));
    return 0;
}
