package study.ywork.boot.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.View;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Map;

/*
 * BeanNameViewResolver类默认被注册，所以可以使用Bean View类
 * Bean的名称就是视图的名称
 */
@Component("myBeanView")
public class MyBeanView implements View {
    private static final String CONTENT_TYPE = "text/html;charset=UTF-8";

    @Override
    public String getContentType() {
        return CONTENT_TYPE;
    }

    @Override
    public void render(Map<String, ?> map, HttpServletRequest req, HttpServletResponse res) throws Exception {
        res.setContentType(CONTENT_TYPE);
        PrintWriter writer = res.getWriter();
        writer.write("myBeanView获得消息: " + map.get("msg"));
    }
}
