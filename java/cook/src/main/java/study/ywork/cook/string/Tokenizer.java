package study.ywork.cook.string;

import java.util.StringTokenizer;

public class Tokenizer {
    public static void main(String args[]) {
        printTokens("apple orange cat dog", " ");
        printTokens("apple, orange   , cat     dog", ", ");
    }
    
    public static void printTokens(String str, String delim) {
        StringTokenizer st = new StringTokenizer(str, delim);
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
    }
}
