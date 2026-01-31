package study.ywork.cook.string;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 计算字符串中子串的数目，一种是完整子串，一种任意子串
 *
 */
public class CountString {
    private static final String STRING = "yyyyyy";
    private static final String SUBSTRING = "yy";

    public static void main(String[] args) {
        int count = countUseIndexOf(STRING, SUBSTRING);
        System.out.printf("countUseIndexOf: '%s' 在 '%s' 出现 %d 次%n", SUBSTRING, STRING, count);
        count = countUseSplit(STRING, SUBSTRING);
        System.out.printf("countUseSplit:   '%s' 在 '%s' 出现 %d 次%n", SUBSTRING, STRING, count);
        count = countUseRegex(STRING, SUBSTRING);
        System.out.printf("countUseRegex:   '%s' 在 '%s' 出现 %d 次%n", SUBSTRING, STRING, count);
    }

    public static int countUseIndexOf(String str, String sub) {

        if (str == null || sub == null) {
            throw new IllegalArgumentException("参数不对");
        }

        if (str.isBlank() || sub.isBlank()) {
            return 0;
        }

        int position = 0;
        int count = 0;
        int n = sub.length();

        while ((position = str.indexOf(sub, position)) != -1) {
            position = position + n;
            count++;
        }

        return count;
    }

    public static int countUseSplit(String str, String sub) {
        if (str == null || sub == null) {
            throw new IllegalArgumentException("参数不对");
        }

        if (str.isBlank() || sub.isBlank()) {
            return 0;
        }

        return str.split(Pattern.quote(sub), -1).length - 1;
    }

    public static int countUseRegex(String str, String sub) {
        if (str == null || sub == null) {
            throw new IllegalArgumentException("参数不对");
        }

        if (str.isBlank() || sub.isBlank()) {
            return 0;
        }

        Pattern pattern = Pattern.compile(Pattern.quote(sub));
        Matcher matcher = pattern.matcher(str);
        int position = 0;
        int count = 0;

        while (matcher.find(position)) {
            position = matcher.start() + 1;
            count++;
        }

        return count;
    }

    private CountString() {
    }
}
