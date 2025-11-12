package study.ywork.web.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * HandlerInterceptorAdapter类作为通用的拦截器实现，只需要继承它并添加自己的业务逻辑
 * 该类实现了AsyncHandlerInterceptor接口，也就是说支持异步请求的拦截处理
 */
public class CommonInterceptor implements AsyncHandlerInterceptor {
    private static final Object CALLABLE_KEY = new Object();
    private static final Object DEFERRED_KEY = new Object();
    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("调用CommonInterceptor::preHandle-线程: " + getThreadName());
        // 生命周期是请求范围
        request.setAttribute("visitorCounter", counter.incrementAndGet());
        HttpSession session = request.getSession(true);

        if (session.getAttribute("startTime") == null) {
            session.setAttribute("startTime", LocalDateTime.now());
        }

        // 注册CallableProcessingInterceptor接口实现对象
        WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
        asyncManager.registerCallableInterceptor(CALLABLE_KEY, new TaskInterceptor());
        asyncManager.registerDeferredResultInterceptor(DEFERRED_KEY, new DeferredInterceptor());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        System.out.println("调用CommonInterceptor::postHandle-线程: " + getThreadName());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        System.out.println("调用CommonInterceptor::afterCompletion-线程: " + getThreadName());
    }

    @Override
    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        System.out.println("调用CommonInterceptor::afterConcurrentHandlingStarted-线程: " + getThreadName());
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}
