package study.ywork.cook.object;

import java.util.Arrays;
import java.util.List;

public class CheckNullDemo {
    public static void main(String[] args) {
        checkNull();
        throwNullException();
    }

    private static void checkNull() {
        List<Integer> numbers = Arrays.asList(1, 2, null, 4, null, 16, 7, null);
        int sum = Numbers.sumIntegers(numbers);
        boolean nulls = Numbers.integersContainsNulls(numbers);
        List<Integer> evens = Numbers.evenIntegers(numbers);

        System.out.println("包含null值? " + nulls);
        System.out.println("求和: " + sum);
        System.out.println("偶数元素列表: " + evens);
    }

    private static void throwNullException() {
        // 抛出NullPointerException异常
        try {
            Flower flower = new Flower("玫瑰", null);
            System.out.println(flower.getColor());
        } catch (NullPointerException e) {
            System.err.println(e.getMessage());
        }
    }
}
