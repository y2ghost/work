package study.ywork.cook.number;

import java.util.BitSet;
import java.util.stream.IntStream;

public class BitUtils {
    private static final long FIRST_BIT = 1L << 0;
    private static final long SECOND_BIT = 1L << 1;
    private static final long THIRD_BIT = 1L << 2;
    private static final long FOURTH_BIT = 1L << 3;
    private static final long FIFTH_BIT = 1L << 4;
    private static final long BIT_55 = 1L << 54;

    public static void main(String[] args) {
        checkBitMask(FIRST_BIT | THIRD_BIT | FIFTH_BIT | BIT_55);
        checkBitSet();
        System.out.println("isPowerOfTwo(5): " + isPowerOfTwo(5));
        System.out.println("isPowerOfTwo(8): " + isPowerOfTwo(8));
    }

    private static void checkBitMask(long bitmask) {
        System.out.println("FIRST_BIT: " + ((bitmask & FIRST_BIT) != 0));
        System.out.println("SECOND_BIT: " + ((bitmask & SECOND_BIT) != 0));
        System.out.println("THIRD_BIT: " + ((bitmask & THIRD_BIT) != 0));
        System.out.println("FOURTh_BIT: " + ((bitmask & FOURTH_BIT) != 0));
        System.out.println("FIFTH_BIT: " + ((bitmask & FIFTH_BIT) != 0));
        System.out.println("BIT_55: " + ((bitmask & BIT_55) != 0));
    }

    private static void checkBitSet() {
        final BitSet bitSet = new BitSet(8);
        // 设置偶数比特位 {0, 2, 4, 6}
        IntStream.range(0, 8).filter(i -> i % 2 == 0).forEach(bitSet::set);
        System.out.println(bitSet);

        bitSet.set(3); // {0, 2, 3, 4, 6}
        System.out.println(bitSet);

        bitSet.set(3, false); // {0, 2, 4, 6}
        System.out.println(bitSet);

        final boolean b = bitSet.get(3); // b = false
        System.out.println(b);

        bitSet.flip(6); // {0, 2, 4}
        System.out.println(bitSet);

        bitSet.set(100); // {0, 2, 4, 100} - 自动扩展
        System.out.println(bitSet);

        System.out.println("测试比特：与或非运算");
        BitSet tmpBitSet = BitSet.valueOf(bitSet.toLongArray());
        tmpBitSet.and(new BitSet(8));
        System.out.println(tmpBitSet);

        tmpBitSet = BitSet.valueOf(bitSet.toLongArray());
        tmpBitSet.or(new BitSet(8));
        System.out.println(tmpBitSet);

        tmpBitSet = BitSet.valueOf(bitSet.toLongArray());
        tmpBitSet.xor(new BitSet(8));
        System.out.println(tmpBitSet);

        tmpBitSet = BitSet.valueOf(bitSet.toLongArray());
        tmpBitSet.andNot(new BitSet(8));
        System.out.println(tmpBitSet);
    }

    private static boolean isPowerOfTwo(int x) {
        return (x != 0) && ((x & (x - 1)) == 0);
    }
}
