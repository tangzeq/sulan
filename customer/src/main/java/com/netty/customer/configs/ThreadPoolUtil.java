package com.netty.customer.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述：线程池
 * 作者：唐泽齐
 */
@Component
public class ThreadPoolUtil {

    @Bean("fixedThreadPool")
    ExecutorService newFixedThreadPool() {
        //本应该是 CPU*2+1
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    @Bean("scheduledThreadPool")
    ExecutorService newScheduledThreadPool() {
        //本应该是 CPU*2+1
        return Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

}