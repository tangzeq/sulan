package com.netty.customer.configs;


import com.netty.customer.storage.BaseMemory;
import com.netty.customer.storage.UserStorage;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.RequestFacade;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.support.AopUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * 功能描述：服务处理
 * 作者：唐泽齐
 */
@Aspect
@Configuration
class LoginFilter {

    private static volatile Set<String> urls;

    @Resource
    public void makeUrl(ConfigurableApplicationContext configurableApplicationContext) {
        if (ObjectUtils.isEmpty(urls)) {
            urls = new HashSet<>();
            String[] names = configurableApplicationContext.getBeanNamesForAnnotation(RestController.class);
            for (String name : names) {
                String[] value = AopUtils.getTargetClass(configurableApplicationContext.getBean(name)).getAnnotation(RequestMapping.class).value();
                Method[] methods = AopUtils.getTargetClass(configurableApplicationContext.getBean(name)).getMethods();
                for (Method method : methods) {
                    RequestMapping re = method.getAnnotation(RequestMapping.class);
                    if (!ObjectUtils.isEmpty(re)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (re.value()[0].startsWith("/") ? "" : "/") + re.value()[0]);
                        continue;
                    }
                    GetMapping get = method.getAnnotation(GetMapping.class);
                    if (!ObjectUtils.isEmpty(get)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (get.value()[0].startsWith("/") ? "" : "/") + get.value()[0]);
                        continue;
                    }
                    PostMapping post = method.getAnnotation(PostMapping.class);
                    if (!ObjectUtils.isEmpty(post)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (post.value()[0].startsWith("/") ? "" : "/") + post.value()[0]);
                        continue;
                    }
                    PutMapping put = method.getAnnotation(PutMapping.class);
                    if (!ObjectUtils.isEmpty(put)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (put.value()[0].startsWith("/") ? "" : "/") + put.value()[0]);
                        continue;
                    }
                    DeleteMapping del = method.getAnnotation(DeleteMapping.class);
                    if (!ObjectUtils.isEmpty(del)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (del.value()[0].startsWith("/") ? "" : "/") + del.value()[0]);
                        continue;
                    }
                    PatchMapping pat = method.getAnnotation(PatchMapping.class);
                    if (!ObjectUtils.isEmpty(pat)) {
                        urls.add((value[0].startsWith("/") ? "" : "/") + value[0] + (pat.value()[0].startsWith("/") ? "" : "/") + pat.value()[0]);
                        continue;
                    }
                }
            }
            urls.remove("/login/login");

        }
    }

    private boolean containsURL(String url) {
        Boolean equ = false;
        for (String s : urls) {
            equ = true;
            String[] us = url.split("/");
            String[] ss = s.split("/");
            if (us.length != ss.length) equ = false;
            else
                for (int i = 0; i < us.length; i++) {
                    if (ss[i].startsWith("{")) continue;
                    if (!ss[i].equals(us[i])) equ = false;
                    ;
                }

            if (equ) break;
        }
        return equ;
    }

    @Around(value = "execution(* com.netty.customer.controller.*.*(..))")
    public Object cache(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        if (containsURL(request.getRequestURI())) {
            BaseMemory user = UserStorage.get((HttpServletRequest) request);
            if (ObjectUtils.isEmpty(user)) Assert.isTrue(false, "请登录");
        }
        return joinPoint.proceed();
    }
}
