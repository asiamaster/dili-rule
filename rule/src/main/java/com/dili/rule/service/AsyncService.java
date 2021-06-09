package com.dili.rule.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * 异步执行任务服务
 */
@Service
public class AsyncService {
    /**
     * 异步执行方法
     *
     * @param supplier
     * @param <T>
     * @return
     */
    @Async
    public <T> CompletableFuture<T> asyncCall(Supplier<T> supplier) {
        return CompletableFuture.completedFuture(supplier.get());
    }
}
