package study.ywork.web.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serial;

@WebServlet(name = "appWebServlet", urlPatterns = "/servlet")
public class AppWebServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter writer = resp.getWriter()) {
            writer.println("AppWebServlet支持例子");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
