#include <iostream>
#include <string>
#include <chrono>
#include <thread>
#include <ctime>

static std::tm createTmStruct(int year, int month, int day,
        int hour, int minutes, int seconds)
{
    struct tm result = {0};
    result.tm_sec = seconds;
    result.tm_min = minutes;
    result.tm_hour = hour;
    result.tm_mday = day;
    result.tm_mon = month - 1;
    result.tm_year = year - 1900;
    return result;
}

static int get_days_in_year(int year)
{
    using namespace std;
    using namespace std::chrono;
    typedef duration<int, ratio_multiply<hours::period, ratio<24> >::type> days;
    std::tm tm_start = createTmStruct(year, 1, 1, 0, 0, 0);
    auto tms = system_clock::from_time_t(std::mktime(&tm_start));
    std::tm tm_end = createTmStruct(year + 1, 1, 1, 0, 0, 0);
    auto tme = system_clock::from_time_t(std::mktime(&tm_end));
    auto diff_in_days = std::chrono::duration_cast<days>(tme - tms);
    return diff_in_days.count();
}

int main(void)
{
    for (int year = 2021; year <= 2024; year++) {
        std::cout << year << "年有" << get_days_in_year(year) << "天" << std::endl;
    }

    auto start = std::chrono::system_clock::now();
    std::this_thread::sleep_for(std::chrono::seconds(2));
    auto end = std::chrono::system_clock::now();
    std::chrono::duration<double> elapsed = end - start;
    std::cout << "花费时间: " << elapsed.count() << "秒" << std::endl;
    return 0;
}

