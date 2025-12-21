#ifndef LIC_DMI_H
#define LIC_DMI_H

typedef struct cJSON cJSON;

cJSON *get_apply_data(void);
const char *get_item_value(const cJSON *jsonData, const char *key);

#endif

