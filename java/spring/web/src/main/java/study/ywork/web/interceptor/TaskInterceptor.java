package study.ywork.web.interceptor;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptor;
import java.util.concurrent.Callable;

/*
 * 演示如何拦截异步请求新建的任务线程，进行特定的逻辑业务处理
 */

public class TaskInterceptor implements CallableProcessingInterceptor {
    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, Callable<T> task) throws Exception {
        System.out.println("调用TaskInterceptor::beforeConcurrentHandling-线程: " + getThreadName());
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, Callable<T> task) throws Exception {
        System.out.println("调用TaskInterceptor::preProcess-线程: " + getThreadName());
    }

    @Override
    public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) throws Exception {
        System.out.println("调用TaskInterceptor::postProcess-线程: " + getThreadName());
    }

    @Override
    public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) throws Exception {
        System.out.println("调用TaskInterceptor::handleTimeout-线程: " + getThreadName());
        return RESULT_NONE;
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
        System.out.println("调用TaskInterceptor::afterCompletion-线程: " + getThreadName());
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}
