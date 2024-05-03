package study.ywork.cook.annotation;

/**
 * 演示@Repeatable注解的用法，等同如下注解的作用
 * @Authors({@Author("张三"), @Author("李四")})
 *
 */
@Author("张三")
@Author("李四")
public class AuthorDemo {
    public static void main(String[] args) {
        Author[] authors = AuthorDemo.class.getAnnotation(Authors.class).value();
        for (Author author : authors) {
            System.out.println(author.value());
        }
    }
}
