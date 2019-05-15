package com.cc;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;

/**
 * @author chenchen
 * @date 2019/5/15 10:44
 * @Content: 继承SpringBootServletInitializer，相当于使用web.xml部署
 */
public class WarStartApplication extends SpringBootServletInitializer {
    /**
     * 重写配置
     *
     * @param builder
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {

        //指向Application运行 启动springboot
        return builder.sources(Application.class);
    }
}
