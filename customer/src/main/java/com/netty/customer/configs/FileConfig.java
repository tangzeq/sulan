package com.netty.customer.configs;

import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;

/**
 * 功能描述：文件配置
 * 作者：唐泽齐
 */
@Configuration
public class FileConfig {
    /**
     * 配置文件上传
     *
     * @return
     */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        //  单个数据大小
        //DataUnit.TERABYTES.size.bytes = 1099511627776
        factory.setMaxFileSize(DataSize.of(Long.MAX_VALUE / 1099511627776l, DataUnit.TERABYTES));
        /// 总上传数据大小
        factory.setMaxRequestSize(DataSize.of(Long.MAX_VALUE / 1099511627776l, DataUnit.TERABYTES));
        return factory.createMultipartConfig();
    }
}
