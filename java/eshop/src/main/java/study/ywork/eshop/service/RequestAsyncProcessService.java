package study.ywork.eshop.service;

import study.ywork.eshop.request.Request;

/**
 * 请求异步执行的接口
 */
public interface RequestAsyncProcessService {
    void process(Request request);
}
