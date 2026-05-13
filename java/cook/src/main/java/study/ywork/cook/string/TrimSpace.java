package study.ywork.cook.string;

/**
 * 移除字符串首尾的空白符 trim函数识别ASCII范围内的空白符 strip函数识别UTF-8范围内的空白符
 */
public class TrimSpace {
    private static final char SPACE = '\u2002';
    private static final String TEXT1 = "\n \n\n  yy  \t \n \r";
    private static final String TEXT2 = "\u2002\n \n\n  yy  \t \n \r\u2002";

    public static void main(String[] args) {
        System.out.printf("\\u2002 是空白字符么? %b%n", Character.isWhitespace(SPACE));
        String trimmed1 = TEXT1.trim();
        String trimmed2 = TEXT2.trim();
        System.out.printf("trim() TEXT1去掉首尾空白符: %s%n", trimmed1);
        System.out.printf("trim() TEXT2去掉首尾空白符: %s%n", trimmed2);

        trimmed1 = TEXT1.strip();
        trimmed2 = TEXT2.strip();
        System.out.printf("strip() TEXT1去掉首尾空白符: %s%n", trimmed1);
        System.out.printf("strip() TEXT2去掉首尾空白符: %s%n", trimmed2);
    }
}
