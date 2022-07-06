package com.substation.acquisition.config;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.druid.spring.boot.autoconfigure.properties.DruidStatProperties;
import com.alibaba.druid.util.Utils;

import java.io.IOException;

/**
 * @author Anzepeng
 * @title: RemoveDruidAdvertisingConfig
 * @projectName eladmin
 * @description: TODO
 * @date 2022/7/6 0006下午 14:46
 */
@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(DruidDataSourceAutoConfigure.class)
@ConditionalOnProperty(name = "spring.datasource.druid.stat-view-servlet.enabled", havingValue = "true", matchIfMissing = true)
public class RemoveDruidAdvertisingConfig {

    /**
     * @Author Anzepeng
     * @Description 去除druid监控
     * @Date 下午 14:47 2022/7/6 0006
     * @Param properties
     * @return FilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean removeDruidAdFilterRegistrationBean(DruidStatProperties properties) {
        /**
         * 获取监控页面参数
         */
        DruidStatProperties.StatViewServlet druidConfig = properties.getStatViewServlet();
        /**
         * 获取common.js位置
         */
        String pattern = druidConfig.getUrlPattern() != null ? druidConfig.getUrlPattern() : "/druid/*";
        String commonJsPattern = pattern.replaceAll("\\*", "js/common.js");
        final String filePath = "support/http/resources/js/common.js";
        /**
         * 利用Filter进行过滤
         */
        Filter filter = new Filter() {
            @Override
            public void init(FilterConfig filterConfig) throws ServletException {
            }
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                chain.doFilter(request, response);
                response.resetBuffer();
                /**
                 * 获取common文件内容
                 */
                String text = Utils.readFromResource(filePath);
                /**
                 * 利用正则表达式删除<footer class="footer">中的<a>标签
                 */
                text = text.replaceAll("<a.*?banner\"></a><br/>", "");
                text = text.replaceAll("powered.*?shrek.wang</a>", "");
                response.getWriter().write(text);
            }

            @Override
            public void destroy() {
            }
        };
        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns(commonJsPattern);
        return registrationBean;
    }
}
