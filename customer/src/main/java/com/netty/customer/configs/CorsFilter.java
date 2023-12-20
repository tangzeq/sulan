package com.netty.customer.configs;


import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.IOException;

/**
 * 功能描述：服务处理
 * 作者：唐泽齐
 */
@Order(1)
@Configuration
public class CorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //跨域设置
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Origin", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Methods", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Max-Age", "3600");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Headers", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Expose-Headers", "*");
        ((HttpServletResponse) response).setHeader("Access-Control-Allow-Credentials", "true");
        chain.doFilter(request, response);
    }
}
