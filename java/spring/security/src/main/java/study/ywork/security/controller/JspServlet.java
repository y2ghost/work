package study.ywork.security.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "jspServlet", urlPatterns = { "/jsp-security" })
public class JspServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter writer = resp.getWriter();
        String servletName = getServletConfig().getServletName();
        writer.printf("Servlet: %s - 处理请求", servletName);
        writer.println("<br/>");
        writer.println("user: " + req.getUserPrincipal().getName());
        writer.println("<br/><a href=\"/security/jsp-page\">JSP页面</a>");
    }
}
