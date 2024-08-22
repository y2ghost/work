package study.ywork.security.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "jspServlet", urlPatterns = {"/jsp-security"})
public class JspServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/html;charset=utf-8");
        PrintWriter writer = resp.getWriter();
        String servletName = getServletConfig().getServletName();
        writer.printf("Servlet: %s - 处理请求", servletName);
        writer.println("<br/>");
        writer.println("user: " + req.getUserPrincipal().getName());
        writer.println("<br/><a href=\"/security/jsp-page\">JSP页面</a>");
    }
}
