package com.mupei.assistant.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.jwt.JwtClaims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.JWTUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
// 需要写配置类WebMvcConfig，并注册该拦截器Interceptor，添加到InterceptorRegistry里面
public class VerifyTokenIntercepter implements HandlerInterceptor {

	@Autowired
	JWTUtil jwtUtil;
	@Autowired
	EncryptUtil encryptUtil;
	// 客户端通过路由和参数调用后端服务器中handler拦截器对象中的方法
	
	// 该方法的返回值是布尔值Boolean类型的，当它返回为false时，表示请求结束，后续的Interceptor和Controller都不会再执行；
	// 当返回值为true时就会继续调用下一个Interceptor的preHandle方法，如果已经是最后一个Interceptor的时候就会是调用当前请求的Controller方法。
	// 请求处理之前的操作
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		// boolean result = obj instanceof Class -> 用来测试一个对象是否为一个类的实例
		// 不是HandlerMethod类型的实例，返回true，请求继续
		if (handler == null || !(handler instanceof HandlerMethod)) // 如果不是映射到方法直接通过       
			return true;

		Method method = ((HandlerMethod) handler).getMethod();
        
		Boolean flag = method.isAnnotationPresent(NoVerifyToken.class); // 判断方法是否被注解
		
		NoVerifyToken annotation = method.getAnnotation(NoVerifyToken.class);
		boolean verifyRequired = true;
		if(annotation != null)
			verifyRequired = annotation.VerifyRequired(); // 获取注解的VerifyRequired属性，默认值为false
        
        if(flag && !verifyRequired) { // 如果有@NoVerifyToken注解并且VerifyRequired属性为false，放行
    		return true;
        }else {
        	log.debug("--开始校验令牌--");
        	
        	/* 校验JWT令牌 */
            String encryptedToken = request.getHeader("Authorization"); // 获取token
    		//-- 以下是用于校验的附加数据 --
            String iss = "com.mupei.assistant"; // 签发者
    		String aud = "com.edu.abtc"; // 接收者
    		
    		final int TIME_MINUTES = 60*24*3; // 过期时限
    		
    		if(encryptedToken == null) {
    			log.debug("--token为空，没有权限访问--");
    			
    			// 没有权限
//    			throw ResponseException.UNAUTHORIZED; // 抛异常，跳到错误页面
                request.setAttribute("msg","无权限请先登录");
                // 获取request返回页面到登录页
                request.getRequestDispatcher("/index.html").forward(request, response); // 转发
    			return false; // 截断请求
    		}
    		
    		//Token解密
    		String token = encryptUtil.decryptWithAES(encryptedToken, encryptUtil.getKeyOfAES());
    		
    		log.debug("Token完成解密[ {} ]", token);
    		
    		JwtClaims claims = jwtUtil.parseToken(token, iss, aud, TIME_MINUTES); // 解析JWT令牌
    		
    		log.debug("解析令牌，获得claims[ {} ]",claims);
    		
    		if(claims == null) {
    			log.debug("--claims为空，没有权限访问--");
    			// 没有权限
//    			throw ResponseException.UNAUTHORIZED; // 抛异常，跳到错误页面
                request.setAttribute("msg","无权限请先登录");
                // 获取request返回页面到登录页
                request.getRequestDispatcher("/index.html").forward(request, response); // 转发
    			return false; // 截断请求
    		}else {
    			log.debug("校验Token成功！");
    		}
        }
        
        return true; 
	}

	// 处理请求完成后视图渲染之前的处理操作
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	// 视图渲染之后的操作
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
