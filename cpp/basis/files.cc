#include <fstream>
#include <iostream>
#include <vector>

using std::string;
using std::ifstream;
using std::getline;
using std::vector;
using std::ws;
using std::cout;
using std::endl;
using std::vector;

struct info_t {
    string name;
    int age;
    float height;

    friend ifstream& operator>>(ifstream& is, info_t& info) {
        is >> ws;
        getline(is, info.name);
        is >> info.age;
        is >> info.height;
        return is;
    }
};

int main(void)
{
    auto file = std::ifstream("test/info.txt");
    vector<info_t> v;

    if (!file.is_open()) {
        cout << "打开文件失败" << endl;
        return -1;
    }

    for (info_t info; file >> info;) {
        v.push_back(info);
    }

    for (auto const& info: v) {
        cout << "    name: " << info.name  << endl;
        cout << "     age: " << info.age   << endl;
        cout << "  height: " << info.height<< endl;
        cout << endl;
    }

    return 0;
}

