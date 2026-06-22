package study.ywork.cook.annotation;

import java.lang.annotation.Target;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;

/**
 * 定义自定义注解，参数值的定义方法类似一般的接口方法定义
 * 
 * @Target元注解指定可用于什么类型
 * @Retention元注解定义了应用程序编译过程或执行期间的注解可见性
 * @Documented元注解定义需要生成文档
 * @Inherited继承父类注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@interface MyAnnotation {
    String key() default "key";
    String value() default "unknown";
}
