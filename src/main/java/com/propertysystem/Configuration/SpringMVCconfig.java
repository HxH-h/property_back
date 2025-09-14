package com.propertysystem.Configuration;

import com.propertysystem.Controller.Interceptor.LoginIntercepter;
import com.propertysystem.Controller.Interceptor.ReqLimitIntercepter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpringMVCconfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] excludePatterns = new String[]{"/swagger-resources/**", "/webjars/**", "/v3/**", "/swagger-ui.html/**",
                "/api", "/api-docs", "/api-docs/**", "/doc.html/**","/favicon.ico",
                "/api/login","/api/register/**","/game/**",
                "/api/getAccessToken"};
        //new 出来的不受容器管理
        registry.addInterceptor(getReqLimitIntercepter())
                .addPathPatterns("/**");
        registry.addInterceptor(getLoginIntercepter())
                .addPathPatterns("/**").excludePathPatterns(excludePatterns);
    }

    @Bean
    public ReqLimitIntercepter getReqLimitIntercepter(){
        return new ReqLimitIntercepter();
    }

    @Bean
    public LoginIntercepter getLoginIntercepter(){
        return new LoginIntercepter();
    }
}
