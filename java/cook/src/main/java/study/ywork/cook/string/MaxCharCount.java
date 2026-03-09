package study.ywork.cook.string;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.Map.Entry.comparingByValue;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * 查找字符串中出现次数最多的字符
 */
public class MaxCharCount {
    private static final int ASCII_CHAR_COUNT = 256;

    // 使用Map的方式查找次数最多的字符
    public static Pair<Character, Integer> maxCharCountMap(String str) {
        if (str == null || str.isBlank()) {
            return Pair.of(Character.MIN_VALUE, -1);
        }

        Map<Character, Integer> charCount = new HashMap<>();
        char[] charArray = str.toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            char currentCh = charArray[i];
            if (!Character.isWhitespace(currentCh)) {
                Integer noCh = charCount.get(currentCh);
                if (noCh == null) {
                    charCount.put(currentCh, 1);
                } else {
                    charCount.put(currentCh, ++noCh);
                }
            }
        }

        int maxCount = Collections.max(charCount.values());
        char maxChar = Character.MIN_VALUE;

        for (Entry<Character, Integer> entry : charCount.entrySet()) {
            if (entry.getValue() == maxCount) {
                maxChar = entry.getKey();
            }
        }

        return Pair.of(maxChar, maxCount);
    }

    // 使用数组的方式查找次数最多的字符
    public static Pair<Character, Integer> maxCharCountArray(String str) {
        if (str == null || str.isBlank()) {
            return Pair.of(Character.MIN_VALUE, -1);
        }

        int maxCount = -1;
        char maxChar = Character.MIN_VALUE;

        char[] charArray = str.toCharArray();
        int[] charCounter = new int[ASCII_CHAR_COUNT];

        for (int i = 0; i < charArray.length; i++) {
            char currentCh = charArray[i];
            if (!Character.isWhitespace(currentCh)) {
                int code = (int) currentCh;
                charCounter[code]++;
                if (charCounter[code] > maxCount) {
                    maxCount = charCounter[code];
                    maxChar = currentCh;
                }
            }

        }

        return Pair.of(maxChar, maxCount);
    }

    // 使用Stream的方式查找次数最多的字符
    public static Pair<Character, Long> maxCharCountStream(String str) {
        if (str == null || str.isBlank()) {
            return Pair.of(Character.MIN_VALUE, -1L);
        }

        return str.chars().filter(c -> Character.isWhitespace(c) == false).mapToObj(c -> (char) c)
            .collect(groupingBy(c -> c, counting())).entrySet().stream().max(comparingByValue())
            .map(p -> Pair.of(p.getKey(), p.getValue())).orElse(Pair.of(Character.MIN_VALUE, -1L));
    }

    private MaxCharCount() {
        // 没事儿需要做
    }
}
