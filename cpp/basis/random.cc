#include <iostream>
#include <random>

int main(void)
{
    std::default_random_engine random_generator;
    std::uniform_int_distribution<int> int_distribution(0, 9);
    int my_distribution[10] = {0};

    for (int i : my_distribution) {
        int result = int_distribution(random_generator);
        my_distribution[result]++;
    }

    for (int i=0; i<=9; i++) {
        std::cout << my_distribution[i] << " ";
    }

   std::cout << std::endl;
   return 0;
}

