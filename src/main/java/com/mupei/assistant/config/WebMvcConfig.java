package com.mupei.assistant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.mupei.assistant.interceptor.VerifyTokenIntercepter;

@Configuration
//public class WebMvcConfig extends WebMvcConfigurerAdapter {
public class WebMvcConfig implements WebMvcConfigurer {
	// 由Spring容器注入该组件，解决在VerifyTokenIntercepter类中无法注入由spring管理的组件的问题
	@Autowired
	VerifyTokenIntercepter verifyTokenIntercepter;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(verifyTokenIntercepter); // 注册拦截器
		//排除静态资源
//		registration.excludePathPatterns("/assistant/static/**");
//		registration.excludePathPatterns();
//		registration.excludePathPatterns();
//		registration.excludePathPatterns();
//		registration.excludePathPatterns();
	}
}
