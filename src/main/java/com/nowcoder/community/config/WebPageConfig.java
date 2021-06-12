package com.nowcoder.community.config;

import com.nowcoder.community.interceptor.DataInterceptor;
import com.nowcoder.community.interceptor.LoginRequiredInterceptor;
import com.nowcoder.community.interceptor.LoginTicketInterceptor;
import com.nowcoder.community.interceptor.MessageInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebPageConfig implements WebMvcConfigurer{

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;
//    @Autowired
//    private LoginRequiredInterceptor loginRequiredInterceptor;
    @Autowired
    private MessageInterceptor messageInterceptor;
    @Autowired
    private DataInterceptor dataInterceptor;

    // 设置默认页面
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //浏览器会发送/dhu请求，来到success.html
        registry.addViewController("").setViewName("index");
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/index").setViewName("index");
//        registry.addViewController("/login").setViewName("login");
//        registry.addViewController("/error").setViewName("login");
    }

    // 这个方法用来注册拦截器，我们自己写好的拦截器需要通过这里添加注册才能生效
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // addPathPatterns("/**") 表示拦截所有的请求，
        // excludePathPatterns("/login", "/register") 表示除了登陆与注册之外，因为登陆注册不需要登陆也可以访问
        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/css/**","/js/**","/img/**");
//        registry.addInterceptor(loginRequiredInterceptor)
//                .excludePathPatterns("/css/**","/js/**","/img/**");
        registry.addInterceptor(messageInterceptor)
                .excludePathPatterns("/css/**","/js/**","/img/**");
        registry.addInterceptor(dataInterceptor)
                .excludePathPatterns("/css/**","/js/**","/img/**");
    }

}
