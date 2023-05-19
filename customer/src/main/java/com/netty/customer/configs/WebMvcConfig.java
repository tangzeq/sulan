package com.netty.customer.configs;

import cn.hutool.core.io.resource.ClassPathResource;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.context.request.async.TimeoutCallableProcessingInterceptor;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 功能描述：异步线程开启
 * 作者：唐泽齐
 */
@Configuration
public class WebMvcConfig extends WebMvcConfigurationSupport {

    /**
     * 前端资源文件配置
     */
    private static final String[] CLASSPATH_RESOURCE_LOCATIONS = {
            "classpath:/META-INF/resources/", "classpath:/resources/",
            "classpath:/static/", "classpath:/public/", "classpath:/templates/"};
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //资源包配置
        if (!registry.hasMappingForPattern("/webjars/**")) {
            registry.addResourceHandler("/webjars/**").addResourceLocations(
                    "classpath:/META-INF/resources/webjars/");
        }
        //前端页面配置
        if (!registry.hasMappingForPattern("/**")) {
            registry.addResourceHandler("/**").addResourceLocations(
                    CLASSPATH_RESOURCE_LOCATIONS);
        }
        //文件访问配置
        if(!registry.hasMappingForPattern("/broadcast/**")) {
            ClassPathResource resource = new ClassPathResource("");
            File file = new File("tmpdir/broadcast");
            String path = file.getAbsolutePath().replaceAll("\\\\", "/");
            registry.addResourceHandler("/broadcast/**")
                    .addResourceLocations("file:"+path+"/"); // 改成你的文件路径
        }
    }


    @SneakyThrows
    public static void main(String[] args) {
        ClassPathResource resource = new ClassPathResource("");
        String projectPath = resource.getFile().getAbsolutePath();
        String path = Paths.get("..", "files").normalize().toString();
        File file = new File("tmpdir/broadcast");
        System.out.println("getPath = " + file.getPath());
        System.out.println("getAbsolutePath = " + file.getAbsolutePath());
        System.out.println("getCanonicalPath = " + file.getCanonicalPath());
        String paths = file.getAbsolutePath().replaceAll("\\\\", "/");
        System.out.println("getCanonicalPath = " + paths);
    }


    /**
     * 异步线程池配置
     * @param configurer
     */
    @Override
    public void configureAsyncSupport(final AsyncSupportConfigurer configurer) {
        configurer.setDefaultTimeout(60 * 1000L);
        configurer.registerCallableInterceptors(timeoutInterceptor());
        configurer.setTaskExecutor(asyncTaskExecutor());
    }
    @Bean
    public TimeoutCallableProcessingInterceptor timeoutInterceptor() {
        return new TimeoutCallableProcessingInterceptor();
    }
    @Bean
    public AsyncTaskExecutor asyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数：线程池创建时候初始化的线程数
        executor.setCorePoolSize(5);
        //最大线程数：线程池最大的线程数，只有在缓冲队列满了之后才会申请超过核心线程数的线程
        executor.setMaxPoolSize(10);
        //缓冲队列：用来缓冲执行任务的队列
        executor.setQueueCapacity(200);
        //允许线程的空闲时间60秒：当超过了核心线程出之外的线程在空闲时间到达之后会被销毁
        executor.setKeepAliveSeconds(60);
        //线程池名的前缀：设置好了之后可以方便我们定位处理任务所在的线程池
        executor.setThreadNamePrefix("taskExecutor-");
        //线程池对拒绝任务的处理策略：这里采用了CallerRunsPolicy策略，当线程池没有处理能力的时候，该策略会直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

}
