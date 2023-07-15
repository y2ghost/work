package study.ywork.cook.annotation;

@UninheritedAnnotationType
class A {
}

@InheritedAnnotationType
class B extends A {
}

class C extends B {
}

// 可以继承类的注解，接口不行
public class InheritedDemo {
    public static void main(String[] args) {
        System.out.println(new A().getClass().getAnnotation(InheritedAnnotationType.class));
        System.out.println(new B().getClass().getAnnotation(InheritedAnnotationType.class));
        System.out.println(new C().getClass().getAnnotation(InheritedAnnotationType.class));
        System.out.println("_________________________________");
        System.out.println(new A().getClass().getAnnotation(UninheritedAnnotationType.class));
        System.out.println(new B().getClass().getAnnotation(UninheritedAnnotationType.class));
        System.out.println(new C().getClass().getAnnotation(UninheritedAnnotationType.class));

    }

}
