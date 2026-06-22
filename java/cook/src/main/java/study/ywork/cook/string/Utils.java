package study.ywork.cook.string;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Utils {
    private static final int ASCII_CHAR_COUNT = 256;
    // 统计包含单位的字符
    private static final Set<Character> allUnits = new HashSet<>(Arrays.asList('a', 'b', 'c', 'd', 'e'));

    public static void main(String[] args) {
        // 验证重复字符
        Map<Character, Integer> duplicates = countcharactersArray("你好好玩碰碰跳");
        System.out.println(Arrays.toString(duplicates.entrySet().toArray()));
        Map<Character, Long> streamDuplicates = countcharactersStream("开开心心玩过家家");
        System.out.println(Arrays.toString(streamDuplicates.entrySet().toArray()));

        // 验证第一个非重复字符
        char result = firstCharNonRepeatedArray("my school is good, my school is ok");
        System.out.println("第一个非重复字符: " + result);
        result = firstCharNonRepeatedMap("我的地盘我做主，我的地盘我管理");
        System.out.println("第一个非重复字符: " + result);

        // 验证反转单词
        String reverseWords = "the good girl  and   boy";
        String resultWords = reverseWordsSplit(reverseWords);
        System.out.println(resultWords);
        resultWords = reverseWordsRegex(reverseWords);
        System.out.println(resultWords);
        resultWords = reverseWordsByStringBuilder(reverseWords);
        System.out.println(resultWords);

        // 验证仅仅包含数字
        final String digits = "123456789";
        final String notDigits = "123456789A";
        boolean isDigits = onlyDigitsBuiltin(digits);
        boolean isNotDigits = onlyDigitsBuiltin(notDigits);
        System.out.printf("onlyDigitsBuiltin isDigits: %b, isNotDigits: %b%n", isDigits, isNotDigits);
        isDigits = onlyDigitsRegex(digits);
        isNotDigits = onlyDigitsRegex(notDigits);
        System.out.printf("onlyDigitsPattern isDigits: %b, isNotDigits: %b%n", isDigits, isNotDigits);
        isDigits = onlyDigitsStream(digits);
        isNotDigits = onlyDigitsStream(notDigits);
        System.out.printf("onlyDigitsStream  isDigits: %b, isNotDigits: %b%n", isDigits, isNotDigits);

        // 验证包含单词的数量
        final String units = "aabbccddgg";
        System.out.printf("countUnitsBuiltin count units: %d%n", countUnitsBuiltin(units));
        System.out.printf("countUnitsStream  count units: %d%n", countUnitsStream(units));
        System.out.printf("countCharStream   count 'a'  : %d%n", countCharStream(units, 'a'));

        // 计算字符串的所有排列
        Stream<String> resultStream = permuteStream("ABC");
        resultStream.forEach(System.out::println);

        // 验证删除重复字符
        final String repeatedChars = "GOOD LUCK LUCK";
        System.out.printf("移除结果: %s%n", removeRepeatedCharsSet(repeatedChars));
        System.out.printf("移除结果: %s%n", removeRepeatedCharsStream(repeatedChars));
    }

    public static Map<Character, Integer> countcharactersArray(String str) {
        if (null == str || str.isBlank()) {
            return Collections.emptyMap();
        }

        Map<Character, Integer> result = new HashMap<>();
        for (char ch : str.toCharArray()) {
            result.compute(ch, (k, v) -> (null == v) ? 1 : ++v);
        }

        return result;
    }

    public static Map<Character, Long> countcharactersStream(String str) {
        if (str == null || str.isBlank()) {
            return Collections.emptyMap();
        }

        return str.chars().mapToObj(c -> (char)c).collect(Collectors.groupingBy(c -> c, Collectors.counting()));
    }

    // 适合ASCII范围内的第一个不重复字符判断
    public static char firstCharNonRepeatedArray(String str) {
        if (null == str || str.isBlank()) {
            return Character.MIN_VALUE;
        }

        int[] flags = new int[ASCII_CHAR_COUNT];
        for (int i = 0; i < flags.length; i++) {
            flags[i] = -1;
        }

        final int REPEATED = -11;
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (-1 == flags[ch]) {
                flags[ch] = i;
            } else {
                flags[ch] = REPEATED;
            }
        }

        int position = Integer.MAX_VALUE;
        for (int i = 0; i < ASCII_CHAR_COUNT; i++) {
            if (flags[i] >= 0) {
                position = Math.min(position, flags[i]);
            }
        }

        return position == Integer.MAX_VALUE ? Character.MIN_VALUE : str.charAt(position);
    }

    // 适合较大范围的字符，比如UTF8
    public static char firstCharNonRepeatedMap(String str) {
        if (str == null || str.isBlank()) {
            return Character.MIN_VALUE;
        }

        Map<Character, Integer> chars = new LinkedHashMap<>();
        for (char ch : str.toCharArray()) {
            chars.compute(ch, (k, v) -> (null == v) ? 1 : ++v);
        }

        char result = Character.MIN_VALUE;
        for (Map.Entry<Character, Integer> entry : chars.entrySet()) {
            if (entry.getValue() == 1) {
                result = entry.getKey();
                break;
            }
        }

        return result;
    }

    // 单词反转但单词顺序不变
    public static String reverseWordsSplit(String str) {
        if (null == str || str.isBlank()) {
            return "";
        }

        final String ws = " +";
        String[] words = str.split(ws);
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            StringBuilder builder = new StringBuilder();
            for (int i = word.length() - 1; i >= 0; i--) {
                builder.append(word.charAt(i));
            }

            result.append(builder).append(" ");
        }

        return result.toString();
    }

    // 单词反转但单词顺序不变
    public static String reverseWordsRegex(String str) {
        if (null == str || str.isBlank()) {
            return "";
        }

        final Pattern pattern = Pattern.compile(" +");
        return pattern.splitAsStream(str).map(w -> new StringBuilder(w).reverse()).collect(Collectors.joining(" "));
    }

    // 使用StringBuilder的反转方法，单词顺序也反转
    public static String reverseWordsByStringBuilder(String str) {
        if (str == null || str.isBlank()) {
            return "";
        }

        return new StringBuilder(str).reverse().toString();
    }

    public static boolean onlyDigitsBuiltin(String str) {
        if (str == null) {
            return false;
        }

        boolean result = true;
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                result = false;
                break;
            }
        }

        return result;
    }

    public static boolean onlyDigitsRegex(String str) {
        if (str == null) {
            return false;
        }

        return str.matches("[0-9]+");
    }

    public static boolean onlyDigitsStream(String str) {
        if (str == null) {
            return false;
        }

        return !str.chars().anyMatch(n -> !Character.isDigit(n));
    }

    // 统计包含allUnits字符的数量
    public static Integer countUnitsBuiltin(String str) {
        if (str == null) {
            return -1;
        }

        str = str.toLowerCase();
        int count = 0;

        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (allUnits.contains(ch)) {
                count++;
            }
        }

        return count;
    }

    // 统计包含allUnits字符的数量
    public static Long countUnitsStream(String str) {
        if (str == null) {
            return -1L;
        }

        str = str.toLowerCase();
        return str.chars().filter(c -> allUnits.contains((char) c)).count();
    }

    // 统计某个字符
    public static long countCharStream(String str, char ch) {
        if (str == null) {
            return -1;
        }

        return str.chars().filter(c -> c == ch).count();
    }

    // 移除所有空白字符
    public static String removeWhitespaces(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return str.replaceAll("\\s", "");
    }

    // 连接字符串，使用String.join方法
    public static String joinBuiltin(char delimiter, String[] args) {
        return String.join(String.valueOf(delimiter), args);
    }

    // 连接字符串，使用StringBuilder.append方法
    public static String joinStringBuilder(char delimiter, String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        StringBuilder result = new StringBuilder();
        int i = 0;

        for (i = 0; i < args.length - 1; i++) {
            result.append(args[i]).append(delimiter);
        }

        result.append(args[i]);
        return result.toString();
    }

    // 连接字符串，使用Arrays.stream方法
    public static String joinStream(char delimiter, String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        return Arrays.stream(args, 0, args.length).collect(Collectors.joining(String.valueOf(delimiter)));
    }

    // 连接字符串，使用StringJoiner.add方法
    public static String joinStringJoiner(char delimiter, String[] args) {
        if (args == null || args.length == 0) {
            return "";
        }

        StringJoiner joiner = new StringJoiner(String.valueOf(delimiter));
        for (String arg : args) {
            joiner.add(arg);
        }

        return joiner.toString();
    }

    // 计算字符串的所有排列
    public static Stream<String> permuteStream(String str) {
        if (str == null || str.isBlank()) {
            return Stream.of("");
        }

        return IntStream.range(0, str.length()).parallel().boxed()
            .flatMap(i -> permuteStream(str.substring(0, i) + str.substring(i + 1)).map(c -> str.charAt(i) + c));
    }

    // 判断回文
    public static boolean isPalindromeBuiltin(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }

        int n = str.length();
        for (int i = 0; i < n / 2; i++) {
            if (str.charAt(i) != str.charAt(n - i - 1)) {
                return false;
            }
        }

        return true;
    }

    // 判断回文，使用StringBuilder.reverse方法
    public static boolean isPalindromeReverse(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }

        return str.equals(new StringBuilder(str).reverse().toString());
    }

    // 判断回文，使用IntStream类处理
    public static boolean isPalindromeStream(String str) {
        if (str == null || str.isBlank()) {
            return false;
        }

        return IntStream.range(0, str.length() / 2).noneMatch(p -> str.charAt(p) != str.charAt(str.length() - p - 1));
    }

    // 使用Set集合的方式移除重复字符
    public static String removeRepeatedCharsSet(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        char[] chArray = str.toCharArray();
        StringBuilder builder = new StringBuilder();
        Set<Character> charSet = new HashSet<>();
        for (char c : chArray) {
            if (charSet.add(c)) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    // 使用Stream的方式移除重复字符
    public static String removeRepeatedCharsStream(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return Arrays.asList(str.split("")).stream().distinct().collect(Collectors.joining());
    }

    // 使用StringBuilder方式移除指定字符
    public static String removeCharBuiltin(String str, char ch) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        char[] chArray = str.toCharArray();
        for (char c : chArray) {
            if (c != ch) {
                builder.append(c);
            }
        }

        return builder.toString();
    }

    // 使用正则表达式方式移除指定字符
    public static String removeCharRegex(String str, char ch) {
        if (str == null || str.isEmpty()) {
            return "";
        }

        return str.replaceAll(Pattern.quote(String.valueOf(ch)), "");
    }

    private Utils() {
        // 工具类、不需要实例化
    }
}
