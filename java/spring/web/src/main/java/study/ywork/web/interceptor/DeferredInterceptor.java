package study.ywork.web.interceptor;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

public class DeferredInterceptor implements DeferredResultProcessingInterceptor {
    @Override
    public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult)
        throws Exception {
        System.out.println("调用DeferredInterceptor::beforeConcurrentHandling-线程: " + getThreadName());
    }

    @Override
    public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        System.out.println("调用DeferredInterceptor::preProcess-线程: " + getThreadName());

    }

    @Override
    public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object concurrentResult)
        throws Exception {
        System.out.println("调用DeferredInterceptor::postProcess-线程: " + getThreadName());

    }

    @Override
    public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        System.out.println("调用DeferredInterceptor::handleTimeout-线程: " + getThreadName());
        return false;
    }

    @Override
    public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
        System.out.println("调用DeferredInterceptor::afterCompletion-线程: " + getThreadName());
    }

    private String getThreadName() {
        return Thread.currentThread().getName();
    }
}
