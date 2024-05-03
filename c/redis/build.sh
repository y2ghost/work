g++ -Wall -Wextra -O2 -g client.cpp -o client
g++ -Wall -Wextra -O2 -g server.cpp \
    avl.cpp hashtable.cpp heap.cpp \
    thread_pool.cpp  zset.cpp -o server -lpthread

exit 0

