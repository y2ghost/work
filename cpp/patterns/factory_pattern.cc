#include <memory>
#include <string>
#include <iostream>
#include <algorithm>

class Animal {
public:
    virtual std::shared_ptr<Animal> clone() const = 0;
    virtual std::string getName() const = 0;
};

class Bear : public Animal {
public:
    virtual std::shared_ptr<Animal> clone() const override {
        return std::make_shared<Bear>(*this);
    }

    virtual std::string getName() const override {
        return "熊";
    }
};

class Cat : public Animal {
public:
    virtual std::shared_ptr<Animal> clone() const override {
        return std::make_shared<Cat>(*this);
    }

    virtual std::string getName() const override {
        return "猫";
    }
};

enum AnimalType {
    BEAR,
    CAT,
};

class AnimalFactory {
public:
    static std::shared_ptr<Animal> getAnimal(AnimalType type) {
        std::shared_ptr<Animal> result = nullptr;
        switch (type) {
            case BEAR: result = std::make_shared<Bear>(); break;
            case CAT:  result = std::make_shared<Cat>(); break;
            default: break;
        }

        return result;
    }
};

int main(void)
{
    std::shared_ptr<Animal> bear = AnimalFactory::getAnimal(BEAR);
    std::shared_ptr<Animal> cat = AnimalFactory::getAnimal(CAT);
    std::cout << bear->getName() << cat->getName() << std::endl;
    return 0;
}

