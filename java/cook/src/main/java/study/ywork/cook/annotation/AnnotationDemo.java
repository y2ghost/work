package study.ywork.cook.annotation;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

/**
 * 演示注解运行时获取信息方法
 * 演示@Setter自定义注解功能
 * 
 * @Setter注解需要编译一个JAR包，然后执行如下命令
 * javac -cp build/libs/cook-1.1.jar \
 *   src/main/java/study/ywork/cook/annotation/AnnotationDemo.java 
 * 程序会报错，本类的main使用@Setter注解报错
 */
@interface MyDefaultAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
@interface MyRuntimeVisibleAnnotation {
}

public class AnnotationDemo {
    @MyDefaultAnnotation
    static class RuntimeCheck1 {
    }

    @MyRuntimeVisibleAnnotation
    static class RuntimeCheck2 {
    }

    @Setter
    public static void main(String[] args) {
        Annotation[] annotationsByType1 = RuntimeCheck1.class.getAnnotations();
        Annotation[] annotationsByType2 = RuntimeCheck2.class.getAnnotations();
        System.out.println("default retention: " + Arrays.toString(annotationsByType1));
        System.out.println("runtime retention: " + Arrays.toString(annotationsByType2));
    }

    @Setter
    private void setValue(String value) {
    }

    @Setter
    public void setString(String value) {
    }
}
