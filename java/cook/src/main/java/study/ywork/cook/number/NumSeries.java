package study.ywork.cook.number;

import java.util.BitSet;
import java.util.stream.IntStream;

public class NumSeries {
    public static void main(String[] args) {
        // 等同区间[1, 12]
        IntStream.rangeClosed(1, 12).forEach(i -> System.out.println("月份# " + i));
        for (int i = 1; i <= months.length; i++) {
            System.out.println("月份# " + i);
        }

        for (String month : months) {
            System.out.println(month);
        }

        // 等同区间[0, months.length)
        IntStream.range(0, months.length).forEach(i -> System.out.println("月份 " + months[i]));
        for (int i = 0; i < months.length; i++) {
            System.out.println("月份 " + months[i]);
        }

        // 部分月份数据集合示例
        BitSet b = new BitSet();
        b.set(0); // January
        b.set(3); // April
        b.set(8); // September

        for (int i = 0; i < months.length; i++) {
            if (b.get(i))
                System.out.println("月份 " + months[i]);
        }

        int[] numbers = { 0, 3, 8 };
        for (int n : numbers) {
            System.out.println("月份: " + months[n]);
        }
    }

    protected static String months[] = { "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December" };
}
