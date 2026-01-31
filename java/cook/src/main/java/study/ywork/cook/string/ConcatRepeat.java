package study.ywork.cook.string;

import java.util.Collections;
import static java.util.stream.Collectors.joining;
import java.util.stream.Stream;

public class ConcatRepeat {
    private static final String TEXT = "yybird";

    public static void main(String[] args) {
        System.out.printf("待重复的字符串: %s%n", TEXT);
        String result = concatRepeatUseStringBuilder(TEXT, 5);
        boolean checked = hasOnlySubstrings(result);
        System.out.printf("concatRepeatUseStringBuilder结果: %s%n仅包含子串: %b%n%n", result, checked);

        result = TEXT.repeat(5);
        checked = hasOnlySubstrings(result);
        System.out.printf("TEXT.repeat结果: %s%n仅包含子串: %b%n%n", result, checked);

        result = String.join("", Collections.nCopies(5, TEXT));
        checked = hasOnlySubstrings(result);
        System.out.printf("String.join结果: %s%n仅包含子串: %b%n%n", result, checked);

        result = Stream.generate(() -> TEXT).limit(5).collect((joining()));
        checked = hasOnlySubstrings(result);
        System.out.printf("Stream.generate结果: %s%n仅包含子串: %b%n%n", result, checked);

        result = String.format("%0" + 5 + "d", 0).replace("0", TEXT);
        checked = hasOnlySubstrings(result);
        System.out.printf("String.format结果: %s%n仅包含子串: %b%n%n", result, checked);

        result = new String(new char[5]).replace("\0", TEXT);
        checked = hasOnlySubstrings(result);
        System.out.printf("new char结果: %s%n仅包含子串: %b%n", result, checked);
    }

    public static String concatRepeatUseStringBuilder(String str, int n) {
        if (str == null || str.isBlank()) {
            return "";
        }

        if (n <= 0) {
            return str;
        }

        if (Integer.MAX_VALUE / n < str.length()) {
            throw new OutOfMemoryError("重复字符串过长");
        }

        StringBuilder sb = new StringBuilder(str.length() * n);
        for (int i = 1; i <= n; i++) {
            sb.append(str);
        }

        return sb.toString();
    }

    // 检查字符串是否由子串构成
    public static boolean hasOnlySubstrings(String str) {
        if (str == null || str.length() < 2) {
            return false;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length() / 2; i++) {
            sb.append(str.charAt(i));
            String resultStr = str.replaceAll(sb.toString(), "");

            if (0 == resultStr.length()) {
                return true;
            }
        }

        return false;
    }
}
