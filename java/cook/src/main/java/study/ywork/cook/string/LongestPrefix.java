package study.ywork.cook.string;

/**
 * 截取字符串数组相同的最长前缀字符串
 *
 */
public class LongestPrefix {
    private static String[] TEXTS = { "abc", "abcd", "abcde", "ab", "abcd", "abcdef" };

    public static void main(String[] args) {
        String result = longestPrefixBuiltin(TEXTS);
        System.out.printf("longestPrefixBuiltin Result: %s%n", result);
        result = longestPrefixBinarySearch(TEXTS);
        System.out.printf("longestPrefixBinarySearch Result: %s%n", result);
    }

    public static String longestPrefixBuiltin(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        if (strs.length == 1) {
            return strs[0];
        }

        int firstLen = strs[0].length();
        for (int prefixLen = 0; prefixLen < firstLen; prefixLen++) {
            char ch = strs[0].charAt(prefixLen);
            for (int i = 1; i < strs.length; i++) {
                if (prefixLen >= strs[i].length() || strs[i].charAt(prefixLen) != ch) {
                    return strs[i].substring(0, prefixLen);
                }
            }
        }

        return strs[0];
    }

    public static String longestPrefixBinarySearch(String[] strs) {
        if (strs == null || strs.length == 0) {
            return "";
        }

        if (strs.length == 1) {
            return strs[0];
        }

        int minStr = Integer.MAX_VALUE;
        for (String str : strs) {
            if (str.length() < minStr) {
                minStr = str.length();
            }
        }

        StringBuilder result = new StringBuilder("");
        int left = 0;
        int right = minStr;
        while (left < right) {
            int middle = left + (right - left) / 2;
            if (isPrefixInAll(strs, left, middle)) {
                result.append(strs[0].substring(left, middle + 1));
                left = middle + 1;
            } else {
                right = middle - 1;
            }
        }

        return result.toString();
    }

    private static boolean isPrefixInAll(String strs[], int start, int end) {
        String str = strs[0];
        for (String currentStr : strs) {
            for (int j = start; j <= end; j++) {
                if (currentStr.charAt(j) != str.charAt(j)) {
                    return false;
                }
            }
        }

        return true;
    }
}
