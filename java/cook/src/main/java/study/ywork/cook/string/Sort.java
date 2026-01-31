package study.ywork.cook.string;

import java.util.Arrays;
import java.util.Comparator;

public class Sort {
    private static final String[] strs = { "one", "two", "three", "four", "five", "six", "seven", "eight", "nine",
        "ten" };

    public enum Direction {
        ASC, DESC;
    }

    public static void main(String[] args) {
        System.out.println("待排序数组:" + Arrays.toString(strs));

        sortUseBuiltin(strs, Direction.DESC);
        System.out.printf("sortUseBuiltin DESC排序: %s%n", Arrays.toString(strs));
        sortUseComparator(strs, Direction.ASC);
        System.out.printf("sortUseComparator ASC排序: %s%n", Arrays.toString(strs));
        String[] result = sortUseStream(strs, Direction.DESC);
        System.out.printf("sortUseStream DESC排序: %s%n", Arrays.toString(result));
    }

    public static void sortUseBuiltin(String[] strs, Direction direction) {
        if (strs == null || direction == null || strs.length == 0) {
            return;
        }

        Comparator<String> comparator = null;
        if (direction.equals(Direction.ASC)) {
            comparator = (s1, s2) -> Integer.compare(s1.length(), s2.length());
        } else {
            comparator = (s1, s2) -> Integer.compare(s2.length(), s1.length());
        }

        Arrays.sort(strs, comparator);
    }

    public static void sortUseComparator(String[] strs, Direction direction) {
        if (strs == null || direction == null || strs.length == 0) {
            return;
        }

        Comparator<String> comparator = null;
        if (direction.equals(Direction.ASC)) {
            comparator = Comparator.comparingInt(String::length);
        } else {
            comparator = Comparator.comparingInt(String::length).reversed();
        }

        Arrays.sort(strs, comparator);
    }

    public static String[] sortUseStream(String[] strs, Direction direction) {
        if (strs == null || direction == null || strs.length == 0) {
            return new String[0];
        }

        Comparator<String> comparator = null;
        if (direction.equals(Direction.ASC)) {
            comparator = Comparator.comparingInt(String::length);
        } else {
            comparator = Comparator.comparingInt(String::length).reversed();
        }

        return Arrays.stream(strs).sorted(comparator).toArray(String[]::new);
    }

    private Sort() {
    }
}
