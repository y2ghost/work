package study.ywork.cook.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListDemo {
    public static void main(String[] args) {
        intersection();
        difference();
        inplace();
        initMap();
    }

    public static void intersection() {
        List<Integer> numbersA = new ArrayList<>();
        List<Integer> numbersB = new ArrayList<>();
        numbersA.addAll(Arrays.asList(new Integer[] { 1, 3, 4, 7, 5, 2 }));
        numbersB.addAll(Arrays.asList(new Integer[] { 13, 32, 533, 3, 4, 2 }));
        System.out.println("A: " + numbersA);
        System.out.println("B: " + numbersB);
        List<Integer> numbersC = new ArrayList<>();
        numbersC.addAll(numbersA);
        numbersC.retainAll(numbersB);
        System.out.println("List A : " + numbersA);
        System.out.println("List B : " + numbersB);
        System.out.println("Common elements between A and B: " + numbersC);
    }

    public static void difference() {
        List<Integer> numbersA = new ArrayList<>();
        List<Integer> numbersB = new ArrayList<>();
        numbersA.addAll(Arrays.asList(new Integer[] { 1, 3, 4, 7, 5, 2 }));
        numbersB.addAll(Arrays.asList(new Integer[] { 13, 32, 533, 3, 4, 2 }));
        System.out.println("A: " + numbersA);
        System.out.println("B: " + numbersB);
        numbersB.removeAll(numbersA);
        System.out.println("B cleared: " + numbersB);
    }

    public static void inplace() {
        List<String> strings = new ArrayList<String>();
        strings.add("Program starting!");
        strings.add("Hello world!");
        strings.add("Goodbye world!");
        strings.set(0, "hi inplace");

        int pos = strings.indexOf("Goodbye world!");
        if (pos >= 0) {
            strings.set(pos, "goodbye!");
        }

        System.out.println(strings);
    }

    public static void initMap() {
        Map<String, Object> m1 = new HashMap<>();
        m1.put("name", "A");
        m1.put("address", "B");
        m1.put("city", "C");
        System.out.println(m1);

        Map<String, Object> m2 = new HashMap<String, Object>() {
            private static final long serialVersionUID = 1L;
            {
                put("name", "A");
                put("address", "B");
                put("city", "C");
            }
        };
        System.out.println(m2);
    }
}
