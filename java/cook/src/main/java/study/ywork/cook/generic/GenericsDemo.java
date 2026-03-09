package study.ywork.cook.generic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class Shoe {
}

class IPhone {
}

interface Fruit {
}

class Apple implements Fruit {
}

class Banana implements Fruit {
}

class GrannySmith extends Apple {
}

// Producer使用extends，消费者使用super
class FruitHelper {
    public void eatAll(Collection<? extends Fruit> fruits) {
    }

    public void addApple(Collection<? super Apple> apples) {
    }
}

public class GenericsDemo {
    public static void main(String[] args) {
        FruitHelper fruitHelper = new FruitHelper();
        List<Fruit> fruits = new ArrayList<Fruit>();
        fruits.add(new Apple());
        fruits.add(new Banana());
        fruitHelper.addApple(fruits);
        fruitHelper.eatAll(fruits);

        Collection<Banana> bananas = new ArrayList<>();
        bananas.add(new Banana());
        // Banana类不是Apple的父类
        // ruitHelper.addApple(bananas);
        fruitHelper.eatAll(bananas);

        Collection<Apple> apples = new ArrayList<>();
        fruitHelper.addApple(apples);
        apples.add(new GrannySmith());
        fruitHelper.eatAll(apples);

        Collection<GrannySmith> grannySmithApples = new ArrayList<>();
        // GrannySmith不是Apple的父类
        // fruitHelper.addApple(grannySmithApples);
        // GrannySmith即是Apple也是Fruit
        apples.add(new GrannySmith());
        fruitHelper.eatAll(grannySmithApples);

        Collection<Object> objects = new ArrayList<>();
        fruitHelper.addApple(objects);
        objects.add(new Shoe());
        objects.add(new IPhone());
        // Object不是Fruit类的子类
        // fruitHelper.eatAll(objects);
    }
}
