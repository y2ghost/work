package study.ywork.cook.annotation;

import java.lang.reflect.Method;

public class MyAnnotationDemo {
    @MyAnnotation
    public void testDefaults() throws Exception {
        Method method = MyAnnotationDemo.class.getMethod("testDefaults");
        MyAnnotation annotation = (MyAnnotation) method.getAnnotation(MyAnnotation.class);
        print(annotation);
    }

    @MyAnnotation(key = "test", value = "good")
    public void testValues() throws Exception {
        Method method = MyAnnotationDemo.class.getMethod("testValues");
        MyAnnotation annotation = (MyAnnotation) method.getAnnotation(MyAnnotation.class);
        print(annotation);
    }

    public void print(MyAnnotation annotation) {
        System.out.println(annotation.key() + " = " + annotation.value());
    }

    public static void main(String[] args) {
        MyAnnotationDemo demo = new MyAnnotationDemo();
        try {
            demo.testDefaults();
            demo.testValues();
        } catch (Exception e) {
            System.err.println("Exception [" + e.getClass().getName() + "] - " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }
}
