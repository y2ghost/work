package study.ywork.web.view;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;

import java.io.PrintWriter;
import java.util.Map;

@Component("appBeanView")
public class BeanNameView implements View {
    @Override
    public String getContentType() {
        return "text/plain;charset=UTF-8";
    }

    @Override
    public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        response.setContentType(getContentType());
        PrintWriter writer = response.getWriter();
        writer.println("这是一个BeanName视图例子");
        writer.println("模型属性列表:");
        for (Map.Entry<String, ?> entry : model.entrySet()) {
            writer.printf("%s = %s%n", entry.getKey(), entry.getValue());
        }
    }
}
