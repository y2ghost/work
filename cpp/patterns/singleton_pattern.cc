// 基础版本
class S {
public:
    static S& getInstance() {
        static S instance;
        return instance;
    }

private:
    S() {};
    // 低版本的方法
    // S(S const &);
    // void operator=(S const&);

public:
    S(S const&) = delete;
    S& operator=(S const&) = delete;
};

// 多线程版本
#include <memory>

class Singleton {
    public:
        Singleton(Singleton const&) = delete;
        Singleton& operator=(Singleton const&) = delete;

        static std::shared_ptr<Singleton> getInstance() {
            static std::shared_ptr<Singleton> s{new Singleton};
            return s;
        }

private:
        Singleton() {}
};

int main(void)
{
    S& s1 = S::getInstance();
    std::shared_ptr<Singleton> s2 = Singleton::getInstance();
    return 0;
}

