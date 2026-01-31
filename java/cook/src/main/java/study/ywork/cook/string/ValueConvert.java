package study.ywork.cook.string;

/**
 * 字符串基本的转换技巧
 */
public class ValueConvert {
    private static final String VALUE_INT = "100";
    private static final String VALUE_LONG = "10000000000";
    private static final String VALUE_FLOAT = "13.145F";
    private static final String VALUE_DOUBLE = "13.14156D";
    private static final String NEGVALUE_INT = "-100";
    private static final String NEGVALUE_LONG = "-10000000000";
    private static final String NEGVALUE_FLOAT = "-13.145F";
    private static final String NEGVALUE_DOUBLE = "-13.14156D";
    private static final String INVALID_NUMBER = "100wrong";

    public static void main(String[] args) {

        // 字符串转Integer
        Integer i1 = Integer.valueOf(VALUE_INT);
        int i2 = Integer.parseInt(VALUE_INT);
        Integer i3 = Integer.valueOf(NEGVALUE_INT);
        int i4 = Integer.parseInt(NEGVALUE_INT);

        // 字符串转Long
        Long l1 = Long.valueOf(VALUE_LONG);
        long l2 = Long.parseLong(VALUE_LONG);
        Long l3 = Long.valueOf(NEGVALUE_LONG);
        long l4 = Long.parseLong(NEGVALUE_LONG);

        // 字符串转Float
        Float f1 = Float.valueOf(VALUE_FLOAT);
        float f2 = Float.parseFloat(VALUE_FLOAT);
        Float f3 = Float.valueOf(NEGVALUE_FLOAT);
        float f4 = Float.parseFloat(NEGVALUE_FLOAT);

        // 字符串转Double
        Double d1 = Double.valueOf(VALUE_DOUBLE);
        double d2 = Double.parseDouble(VALUE_DOUBLE);
        Double d3 = Double.valueOf(NEGVALUE_DOUBLE);
        double d4 = Double.parseDouble(NEGVALUE_DOUBLE);

        System.out.printf("VALUE_INT:%s    i1:%d, i2:%d%n", VALUE_INT, i1, i2);
        System.out.printf("NEGVALUE_INT:%s i3:%d, i4:%d%n", VALUE_INT, i3, i4);
        System.out.printf("VALUE_LONG:%s    l1:%d, l2:%d%n", VALUE_LONG, l1, l2);
        System.out.printf("NEGVALUE_LONG:%s l3:%d, l4:%d%n", NEGVALUE_LONG, l3, l4);
        System.out.printf("VALUE_FLOAT:%s    f1:%f, f2:%f%n", VALUE_FLOAT, f1, f2);
        System.out.printf("NEGVALUE_FLOAT:%s f3:%f, f4:%f%n", NEGVALUE_FLOAT, f3, f4);
        System.out.printf("VALUE_DOUBLE:%s    d1:%f, d2:%f%n", VALUE_DOUBLE, d1, d2);
        System.out.printf("NEGVALUE_DOUBLE:%s d3:%f, d4:%f%n", NEGVALUE_DOUBLE, d3, d4);

        try {
            i1 = Integer.valueOf(INVALID_NUMBER);
        } catch (NumberFormatException e) {
            System.err.println(e);
        }

        try {
            i2 = Integer.parseInt(INVALID_NUMBER);
        } catch (NumberFormatException e) {
            System.err.println(e);
        }
    }
}
