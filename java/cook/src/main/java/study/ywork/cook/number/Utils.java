package study.ywork.cook.number;

import java.math.BigInteger;
import java.util.function.BinaryOperator;

public class Utils {
    private static final String NRI = "255500";
    private static final String NRL = "25550049303";

    public static void main(String[] args) {
        parseInt();
        parseLong();
        compareInt();
        division();
        isFinite();
        logical();
        bigIntegerToPrimitive();
        longToInt();
        floorModDiv();
        NextFloatingPointValue();
        multiply();
        fma();
        integerRadix();
    }

    private static void parseInt() {
        int result1i = Integer.parseUnsignedInt(NRI);
        int result2i = Integer.parseUnsignedInt(NRI, Character.MAX_RADIX);
        int result3i = Integer.parseUnsignedInt(NRI, 1, 4, Character.MAX_RADIX);

        System.out.println("1i: " + result1i);
        System.out.println("2i: " + result2i);
        System.out.println("3i: " + result3i);

        long result1l = Long.parseUnsignedLong(NRL);
        long result2l = Long.parseUnsignedLong(NRL, Character.MAX_RADIX);
        long result3l = Long.parseUnsignedLong(NRL, 1, 4, Character.MAX_RADIX);

        System.out.println("1l: " + result1l);
        System.out.println("2l: " + result2l);
        System.out.println("3l: " + result3l);

        // 2147483648 is Integer.MAX_VALUE + 1
        int maxValuePlus = Integer.parseUnsignedInt("2147483648");
        System.out.println("Integer.MAX_VALUE加1: " + maxValuePlus);
    }

    public static void parseLong() {
        long result = Integer.toUnsignedLong(Integer.MIN_VALUE);
        System.out.println("Integer.MIN_VALUE: " + Integer.MIN_VALUE);
        System.out.println("Integer.MIN_VALUE转无符号整数: " + result);
        System.out.println();

        int result1 = Short.toUnsignedInt(Short.MIN_VALUE);
        int result2 = Short.toUnsignedInt(Short.MAX_VALUE);
        System.out.println("Short.MIN_VALUE: " + Short.MIN_VALUE + " Short.MAX_VALUE: " + Short.MAX_VALUE);
        System.out.println("Short.MIN_VALUE转无符号整数: " + result1);
        System.out.println("Short.MAX_VALUE转无符号整数: " + result2);
    }

    public static void compareInt() {
        int resultSigned = Integer.compare(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int resultUnsigned = Integer.compareUnsigned(Integer.MIN_VALUE, Integer.MAX_VALUE);

        System.out.println("有符号整数值: " + Integer.MIN_VALUE + ", " + Integer.MAX_VALUE);
        System.out.println("-----------------------------------------------");
        System.out.println("有符号比较结果: " + resultSigned);
        System.out.println("无符号比较结果: " + resultUnsigned);
    }

    private static void division() {
        System.out.println("除法示例:\n--------------");
        int divisionSignedMinMax = Integer.MIN_VALUE / Integer.MAX_VALUE;
        int divisionSignedMaxMin = Integer.MAX_VALUE / Integer.MIN_VALUE;

        System.out.println("有符号除法MIN/MAX: " + divisionSignedMinMax);
        System.out.println("有符号除法MAX/MIN: " + divisionSignedMaxMin);

        int divisionUnsignedMinMax = Integer.divideUnsigned(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int divisionUnsignedMaxMin = Integer.divideUnsigned(Integer.MAX_VALUE, Integer.MIN_VALUE);

        System.out.println("无符号除法MIN/MAX: " + divisionUnsignedMinMax);
        System.out.println("无符号除法MAX/MIN: " + divisionUnsignedMaxMin);

        System.out.println("\n取余运算:\n--------------");
        int moduloSignedMinMax = Integer.MIN_VALUE % Integer.MAX_VALUE;
        int moduloSignedMaxMin = Integer.MAX_VALUE % Integer.MIN_VALUE;

        System.out.println("有符号取余MIN/MAX: " + moduloSignedMinMax);
        System.out.println("有符号取余MAX/MIN: " + moduloSignedMaxMin);

        int moduloUnsignedMinMax = Integer.remainderUnsigned(Integer.MIN_VALUE, Integer.MAX_VALUE);
        int moduloUnsignedMaxMin = Integer.remainderUnsigned(Integer.MAX_VALUE, Integer.MIN_VALUE);

        System.out.println("无符号取余MIN/MAX: " + moduloUnsignedMinMax);
        System.out.println("无符号取余MAX/MIN: " + moduloUnsignedMaxMin);
    }

    private static void isFinite() {
        Float f1 = 4.5f;
        boolean f1f = Float.isFinite(f1);

        Float f2 = f1 / 0;
        boolean f2f = Float.isFinite(f2);

        Float f3 = 0f / 0f;
        boolean f3f = Float.isFinite(f3);

        System.out.println("单精度浮点数是否有限数值:\n-------------------");
        System.out.println(f1 + " is finite? " + f1f);
        System.out.println(f2 + " is finite? " + f2f);
        System.out.println(f3 + " is finite? " + f3f);

        Double d1 = 0.000333411333d;
        boolean d1f = Double.isFinite(d1);

        Double d2 = d1 / 0;
        boolean d2f = Double.isFinite(d2);

        Double d3 = Double.POSITIVE_INFINITY * 0;
        boolean d3f = Double.isFinite(d3);

        System.out.println("\n双精度浮点数是否有限数值:\n-------------------");
        System.out.println(d1 + " is finite? " + d1f);
        System.out.println(d2 + " is finite? " + d2f);
        System.out.println(d3 + " is finite? " + d3f);
    }

    private static void logical() {
        int s = 10;
        int m = 21;

        // if (s > m && m < 50)
        if (Boolean.logicalAnd(s > m, m < 50)) {
            System.out.println("Boolean.logicalAnd returned true");
        } else {
            System.out.println("Boolean.logicalAnd returned false");
        }

        // if (s > m || m < 50)
        if (Boolean.logicalOr(s > m, m < 50)) {
            System.out.println("Boolean.logicalOr returned true");
        } else {
            System.out.println("Boolean.logicalOr returned false");
        }

        // if (s > m ^ m < 50)
        if (Boolean.logicalXor(s > m, m < 50)) {
            System.out.println("Boolean.logicalXor returned true");
        } else {
            System.out.println("Boolean.logicalXor returned false");
        }

        if (Boolean.logicalAnd(Boolean.logicalOr(s > m, m < 50), Boolean.logicalOr(s <= m, m > 50))) {
            System.out.println("Combination returned true");
        } else {
            System.out.println("Combination returned false");
        }
    }

    private static void bigIntegerToPrimitive() {
        BigInteger nr = BigInteger.valueOf(Long.MAX_VALUE);
        long nrLong = nr.longValue();
        System.out.println(nr + "转换为long: " + nrLong);

        int nrInt = nr.intValue();
        System.out.println(nr + "转换为int: " + nrInt);

        short nrShort = nr.shortValue();
        System.out.println(nr + "转换为short: " + nrShort);

        byte nrByte = nr.byteValue();
        System.out.println(nr + "转换为byte: " + nrByte);

        long nrExactLong = nr.longValueExact();
        System.out.println(nr + "精确转换为long: " + nrExactLong);

        try {
            int nrExactInt = nr.intValueExact(); // ArithmeticException异常
            System.out.println(nr + "精确转换为int: " + nrExactInt);

            short nrExactShort = nr.shortValueExact(); // ArithmeticException异常
            System.out.println(nr + "精确转换为short: " + nrExactShort);

            byte nrExactByte = nr.byteValueExact(); // ArithmeticException异常
            System.out.println(nr + "精确转换为byte: " + nrExactByte);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
        }
    }

    public static void longToInt() {
        long nrLong = Integer.MAX_VALUE;
        long nrMaxLong = Long.MAX_VALUE;

        int intNrCast = (int) nrLong;
        int intNrMaxCast = (int) nrMaxLong;
        System.out.println("转换Integer.MAX_VALUE: " + intNrCast);
        System.out.println("转换Long.MAX_VALUE: " + intNrMaxCast);

        int intNrValue = Long.valueOf(nrLong).intValue();
        int intNrMaxValue = Long.valueOf(nrMaxLong).intValue();

        System.out.println();
        System.out.println("intValue() Integer.MAX_VALUE: " + intNrValue);
        System.out.println("intValue() Long.MAX_VALUE: " + intNrMaxValue);

        try {
            int intNrExact = Math.toIntExact(nrLong);
            int intNrMaxExact = Math.toIntExact(nrMaxLong); // ArithmeticException异常

            System.out.println();
            System.out.println("intNrExact: " + intNrExact);
            System.out.println("intNrMaxExact: " + intNrMaxExact);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
        }
    }

    private static void floorModDiv() {
        int x = -222;
        int y = 14;

        System.out.println("Dividend: " + x + " Divisor: " + y);
        System.out.println();
        int z = Math.floorDiv(x, y); // -222/14 = -15.85, so output is -16
        System.out.println("Floor division via '/' is: " + (x / y));
        System.out.println("Floor division via 'floorDiv()' is: " + z);

        System.out.println();
        int m = Math.floorMod(x, y);
        System.out.println("Floor modulus vis '%' is: " + (x % y));
        System.out.println("Floor modulus via 'floorMod()' is: " + m);
    }

    private static void NextFloatingPointValue() {
        float f = 0.1f;
        float nextdownf = Math.nextDown(f);
        float nextupf = Math.nextUp(f);

        System.out.println("float " + f + " next down is " + nextdownf);
        System.out.println("float " + f + " next up is " + nextupf);

        double d = 0.1d;
        double nextdownd = Math.nextDown(d);
        double nextupd = Math.nextUp(d);

        System.out.println("double " + d + " next down is " + nextdownd);
        System.out.println("double " + d + " next up is " + nextupd);
    }

    private static void multiply() {
        int x = Integer.MAX_VALUE;
        int y = Integer.MAX_VALUE;

        int z = x * y;
        System.out.println(x + " * " + y + " via '*' operator is: " + z);

        long zFull = Math.multiplyFull(x, y);
        System.out.println(x + " * " + y + " via Math.multiplyFull() is: " + zFull);
        try {
            // ArithmeticException异常
            int zExact = Math.multiplyExact(x, y);
            System.out.println(x + " * " + y + " via Math.multiplyExact() is: " + zExact);

            // ArithmeticException异常
            BinaryOperator<Integer> operator = Math::multiplyExact;
            int zExactBo = operator.apply(x, y);
            System.out.println(x + " * " + y + " via BinaryOperator is: " + zExactBo);
        } catch (ArithmeticException e) {
            System.err.println(e.getMessage());
        }
    }

    // Fused Multiply Add运算
    private static void fma() {
        double x = 49.29d;
        double y = -28.58d;
        double z = 33.63d;
        double q = (x * y) + z;
        double fma = Math.fma(x, y, z);

        System.out.println("non-fma: " + q);
        System.out.println("fma: " + fma);
    }

    // 字符串转换为特定进制整数
    private static void integerRadix() {
        String input = "101010";
        int number = 42;

        for (int radix : new int[] { 2, 8, 10, 16, 36 }) {
            System.out.printf("%s转换为%d进制的数: %d; ", input, radix, Integer.valueOf(input, radix));
            System.out.printf("%d转换为%d进制的字符串: %s%n", number, radix, Integer.toString(number, radix));
        }
    }
}
