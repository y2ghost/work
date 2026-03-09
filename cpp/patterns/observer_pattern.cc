#include <iostream>
#include <vector>
#include <algorithm>

class Subject;

// 观察者
class Observer {
public:
    virtual ~Observer() = default;
    virtual void update(Subject&) = 0;
};

// 观察对象
class Subject {
public:
    virtual ~Subject() = default;
    void attach(Observer& o) { observers.push_back(&o); }
    void detach(Observer& o) {
        observers.erase(std::remove(observers.begin(), observers.end(), &o));
    }
    void notify() {
        for (auto* o : observers) {
            o->update(*this);
        }
    }

private:
    std::vector<Observer*> observers;
};

class ClockTimer : public Subject {
public:
    void setTime(int hour, int minute, int second) {
        this->hour = hour;
        this->minute = minute;
        this->second = second;
        notify();
    }

    int getHour() const { return hour; }
    int getMinute() const { return minute; }
    int getSecond() const { return second; }

private:
    int hour;
    int minute;
    int second;
};

class DigitalClock : public Observer {
public:
    explicit DigitalClock(ClockTimer& s) : subject(s) { subject.attach(*this); }
    ~DigitalClock() { subject.detach(*this); }

    void update(Subject& s) override {
        if (&s == &subject) {
            draw();
        }
    }

    void draw() {
        int hour = subject.getHour();
        int minute = subject.getMinute();
        int second = subject.getSecond();
        std::cout << "数字时间 " << hour << ":" << minute << ":" << second << std::endl;
    }

private:
    ClockTimer& subject;
};

class AnalogClock : public Observer {
public:
    explicit AnalogClock(ClockTimer& s) : subject(s) { subject.attach(*this); }
    ~AnalogClock() { subject.detach(*this); }

    void update(Subject& s) override {
        if (&s == &subject) {
            draw();
        }
    }

    void draw() {
        int hour = subject.getHour();
        int minute = subject.getMinute();
        int second = subject.getSecond();
        std::cout << "模拟时间 " << hour << ":" << minute << ":" << second << std::endl;
    }

private:
    ClockTimer& subject;
};

int main(void)
{
    ClockTimer timer;
    DigitalClock digitalClock(timer);
    AnalogClock analogClock(timer);
    timer.setTime(11, 11, 11);
    return 0;
}

