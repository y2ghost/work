package study.ywork.cook.string;

import java.util.regex.Pattern;

public class CheckSubstring {
    private static final String TEXT = "I am programmer";
    private static final String SUBTEXT = "programmer";

    public static void main(String[] args) {
        System.out.printf("搜索文本: %s%n", TEXT);
        System.out.printf("搜索字符串: %s%n",SUBTEXT);
        boolean checked = TEXT.contains(SUBTEXT);
        System.out.printf("contains包含? %b%n", checked);
        checked = -1 != TEXT.indexOf(SUBTEXT);
        System.out.printf("indexOf包含? %b%n", checked);
        checked = TEXT.matches("(?i).*" + Pattern.quote(SUBTEXT) + ".*");
        System.out.printf("matches包含? %b%n", checked);
    }

    private CheckSubstring() {
    }
}
