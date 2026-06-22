#include <stdio.h>

static int binarySearch(int array[], int length, int searchValue)
{
    int low = 0;
    int high = length;
    
    while (low <= high) {
        int mid = (low + high) / 2;
        if (searchValue == array[mid]) {
            return mid;
        }

        if (searchValue > array[mid]) {
            low = mid + 1;
        } else {
            high = mid - 1;
        }
    }

    return -1;
}
int main()
{
    int scores[] = {30, 40, 50, 70, 85, 90, 100};
    int length = sizeof(scores) / sizeof(scores[0]);
    int searchValue = 40;
    int position = binarySearch(scores, length, searchValue);
    printf("%d 位置: %d",searchValue, position);
    printf("\n-----------------------------\n");
    searchValue = 90;
    position = binarySearch(scores, length, searchValue);
    printf("%d 位置: %d\n",searchValue, position);
    return 0;
}
