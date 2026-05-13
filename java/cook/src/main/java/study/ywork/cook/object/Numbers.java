package study.ywork.cook.object;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

// 示例Objects.isNull和Objects.nonNull的用法
public final class Numbers {
    public static int sumIntegers(List<Integer> integers) {
        if (Objects.isNull(integers)) {
            throw new IllegalArgumentException("List cannot be null");
        }

        return integers.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).sum();
    }

    public static boolean integersContainsNulls(List<Integer> integers) {
        if (Objects.isNull(integers)) {
            return false;
        }

        return integers.stream().anyMatch(Objects::isNull);
    }

    public static List<Integer> evenIntegers(List<Integer> integers) {
        if (integers == null) {
            return Collections.emptyList();
        }

        List<Integer> evens = new ArrayList<>();
        for (Integer nr : integers) {
            if (nr != null && nr % 2 == 0) {
                evens.add(nr);
            }
        }

        return evens;
    }

    private Numbers() {
        throw new AssertionError("cannot be instantiated");
    }
}
