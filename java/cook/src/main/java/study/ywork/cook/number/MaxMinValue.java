package study.ywork.cook.number;

import java.util.function.BinaryOperator;

public class MaxMinValue {
    // 整数测试数据
    private static final int I1 = -45;
    private static final int I2 = -15;
    // 长整型测试数据
    private static final long L1 = 123L;
    private static final long L2 = 3L;
    // 单精度浮点数
    private static final float F1 = 33.34F;
    private static final float F2 = 33.213F;
    // 双精度浮点数
    private static final double D1 = 0.023844D;
    private static final double D2 = 0.35468856D;

    public static void main(String[] args) {
        // 比较整数
        int minII = Integer.min(I1, I2);
        int minIM = Math.min(I1, I2);
        int minIB = BinaryOperator.minBy(Integer::compare).apply(I1, I2);

        int maxII = Integer.max(I1, I2);
        int maxIM = Math.max(I1, I2);
        int maxIB = BinaryOperator.maxBy(Integer::compare).apply(I1, I2);

        System.out.println("\nCompare two integers:");
        System.out.printf("(%d, %d): minBy:%d maxBy: %d%n", I1, I2, minIB, maxIB);
        System.out.printf("(%d, %d): Math.min:%d Math.max: %d%n", I1, I2, minIM, maxIM);
        System.out.printf("(%d, %d): Integer.min:%d Math.max: %d%n", I1, I2, minII, maxII);

        // 比较长整数
        long minLL = Long.min(L1, L2);
        long minLM = Math.min(L1, L2);
        long minLB = BinaryOperator.minBy(Long::compare).apply(L1, L2);

        long maxLL = Long.max(L1, L2);
        long maxLM = Math.max(L1, L2);
        long maxLB = BinaryOperator.maxBy(Long::compare).apply(L1, L2);

        System.out.println("\nCompare two longs:");
        System.out.printf("(%d, %d): minBy:%d maxBy: %d%n", L1, L2, minLB, maxLB);
        System.out.printf("(%d, %d): Math.min:%d Math.max: %d%n", L1, L2, minLM, maxLM);
        System.out.printf("(%d, %d): Integer.min:%d Math.max: %d%n", L1, L2, minLL, maxLL);

        // 比较单精度浮点数
        float minFF = Float.min(F1, F2);
        float minFM = Math.min(F1, F2);
        float minFB = BinaryOperator.minBy(Float::compare).apply(F1, F2);

        float maxFF = Float.max(F1, F2);
        float maxFM = Math.max(F1, F2);
        float maxFB = BinaryOperator.maxBy(Float::compare).apply(F1, F2);

        System.out.println("\nCompare two floats:");
        System.out.printf("(%f, %f): minBy:%f maxBy: %f%n", F1, F2, minFB, maxFB);
        System.out.printf("(%f, %f): Math.min:%f Math.max: %f%n", F1, F2, minFM, maxFM);
        System.out.printf("(%f, %f): Integer.min:%f Math.max: %f%n", F1, F2, minFF, maxFF);

        // 比较双精度浮点数
        double minDD = Double.min(D1, D2);
        double minDM = Math.min(D1, D2);
        double minDB = BinaryOperator.minBy(Double::compare).apply(D1, D2);

        double maxDD = Double.max(D1, D2);
        double maxDM = Math.max(D1, D2);
        double maxDB = BinaryOperator.maxBy(Double::compare).apply(D1, D2);

        System.out.println("\nCompare two doubles:");
        System.out.printf("(%f, %f): minBy:%f maxBy: %f%n", D1, D2, minDB, maxDB);
        System.out.printf("(%f, %f): Math.min:%f Math.max: %f%n", D1, D2, minDM, maxDM);
        System.out.printf("(%f, %f): Integer.min:%f Math.max: %f%n", D1, D2, minDD, maxDD);
    }
}
