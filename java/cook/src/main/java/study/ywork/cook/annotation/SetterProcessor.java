package study.ywork.cook.annotation;

import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;

/**
 * 注解处理器必须定义配置文件
 * 配置路径: META-INF/services/javax.annotation.processing.Processor
 * 配置内容: study.ywork.cook.annotation.SetterProcessor
 *
 */
@SupportedAnnotationTypes({ "study.ywork.cook.annotation.Setter" })
@SupportedSourceVersion(SourceVersion.RELEASE_11)
public class SetterProcessor extends AbstractProcessor {
    private Messager messager;

    @Override
    public void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(Setter.class);
        for (Element element : annotatedElements) {
            if (element.getKind() == ElementKind.METHOD) {
                checkMethod((ExecutableElement) element);
            }
        }
        return false;
    }

    private void checkMethod(ExecutableElement method) {
        String name = method.getSimpleName().toString();
        if (!name.startsWith("set")) {
            printError(method, "setter函数名称必须以set开头");
        } else if (name.length() == 3) {
            printError(method, "setter函数名称必须大于3字符");
        } else if (Character.isLowerCase(name.charAt(3))) {
            if (method.getParameters().size() != 1) {
                printError(method, "setter函数名称驼峰风格");
            }
        }

        if (!method.getModifiers().contains(Modifier.PUBLIC)) {
            printError(method, "setter函数必须公开");
        }

        if (method.getModifiers().contains(Modifier.STATIC)) {
            printError(method, "setter函数不能静态");
        }
    }

    private void printError(Element element, String message) {
        messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}
