# 编译pcre2-10.44示例
tar -xvf ~/files/pcre2-10.44.tar.bz2
cd pcre2-10.44/
./configure --enable-pcre2grep-libz --enable-pcre2grep-libbz2 --enable-pcre2test-libedit --prefix=$HOME/progs/
make
make check
make install

