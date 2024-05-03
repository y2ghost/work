package study.ywork.cook.string;

import java.util.Arrays;

/**
 * 判断字符串相同，只要有着相同的字母，不管顺序
 *
 */
public class CheckSameChars {
    private static final String ANAGRAM1 = "good boy";
    private static final String ANAGRAM2 = "d\n yob o go";
    private static final int ASCII_CHAR_COUNT = 256;

    public static void main(String[] args) {
        boolean isAnagram = isAnagramUseSort(ANAGRAM1, ANAGRAM2);
        System.out.printf("isAnagramUseSort: 字母异位词? %b%n", isAnagram);
        isAnagram = isAnagramUseCounter(ANAGRAM1, ANAGRAM2);
        System.out.printf("isAnagramUseCounter: 字母异位词? %b%n", isAnagram);
        isAnagram = isAnagramUseSorted(ANAGRAM1, ANAGRAM2);
        System.out.printf("isAnagramUseSorted: 字母异位词? %b%n", isAnagram);
    }

    public static boolean isAnagramUseSort(String str1, String str2) {
        if (str1 == null || str2 == null || str1.isBlank() || str2.isBlank()) {
            return false;
        }

        char[] strArray1 = str1.replaceAll("\\s", "").toLowerCase().toCharArray();
        char[] strArray2 = str2.replaceAll("\\s", "").toLowerCase().toCharArray();

        if (strArray1.length != strArray2.length) {
            return false;
        }

        Arrays.sort(strArray1);
        Arrays.sort(strArray2);
        return Arrays.equals(strArray1, strArray2);
    }

    // 适合ASCII的字符，统计字符的数目，一个加，一个减最终为零
    public static boolean isAnagramUseCounter(String str1, String str2) {
        if (str1 == null || str2 == null || str1.isBlank() || str2.isBlank()) {
            return false;
        }

        int[] counter = new int[ASCII_CHAR_COUNT];
        char[] strArray1 = str1.replaceAll("\\s", "").toLowerCase().toCharArray();
        char[] strArray2 = str2.replaceAll("\\s", "").toLowerCase().toCharArray();

        if (strArray1.length != strArray2.length) {
            return false;
        }

        for (int i = 0; i < strArray1.length; i++) {
            counter[strArray1[i]]++;
            counter[strArray2[i]]--;
        }

        for (int i = 0; i < counter.length; i++) {
            if (counter[i] != 0) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAnagramUseSorted(String str1, String str2) {
        if (str1 == null || str2 == null || str1.isBlank() || str2.isBlank()) {
            return false;
        }

        str1 = str1.replaceAll("\\s", "").toLowerCase();
        str2 = str2.replaceAll("\\s", "").toLowerCase();

        if (str1.length() != str2.length()) {
            return false;
        }

        return Arrays.equals(str1.chars().sorted().toArray(), str2.chars().sorted().toArray());
    }

    private CheckSameChars() {
    }
}
