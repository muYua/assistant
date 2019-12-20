package com.mupei.assistant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.mupei.assistant.interceptor.VerifyTokenInterceptor;

@Configuration
//public class WebMvcConfig extends WebMvcConfigurerAdapter {
public class WebMvcConfig implements WebMvcConfigurer {
	// 由Spring容器注入该组件，解决在verifyTokenInterceptor类中无法注入由spring管理的组件的问题
	@Autowired
	VerifyTokenInterceptor verifyTokenInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration registration = registry.addInterceptor(verifyTokenInterceptor);// 注册拦截器
		//排除
//		registration.addPathPatterns("/**");
//		registration.excludePathPatterns("/assistant/activateVerifyCode/**");
//		registration.excludePathPatterns("/**.js", "/**.css", "/**.jpg", "/**.png", "/**.ico");
	}
}
