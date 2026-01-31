#include <dlfcn.h>
#include <stdio.h>
#include <stdlib.h>

typedef struct pk_license_t {
    char* data;
    char* sig;
} pk_license_t;

static const char *TOOL_SO = "/opt/license-tools/lib/license-tools.so";

int main(void)
{
    pk_license_t (*load_license_file)(void);
    int (*verify)(pk_license_t license);

    void *libHandle = dlopen(TOOL_SO, RTLD_LAZY);
    if (NULL == libHandle) {
        fprintf(stderr, "dlopen: %s\n", dlerror());
        return -1;
    }

    dlerror();
    *(void **)(&load_license_file) = dlsym(libHandle, "load_license_file");
    *(void **)(&verify) = dlsym(libHandle, "verify");
    pk_license_t license = (*load_license_file)();

    if (NULL == license.data) {
        fprintf(stderr, "无法加载许可数据\n");
        return -1;
    }

    int rc = (*verify)(license);
    const char *err = "有效";

    if (-1 == rc) {
        err = "无效";
    } else if (-2 == rc) {
        err = "过期";
    }

    fprintf(stdout, "授权状态:\n%s\n", err);
    free(license.data);
    free(license.sig);
    dlclose(libHandle);
    return 0;
}

