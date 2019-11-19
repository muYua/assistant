package com.mupei.assistant.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

// 用于验证JWT令牌
@Component
@Retention(RUNTIME) // 生命周期：注解不仅被保存到class文件中，jvm加载class文件之后，仍然存在
@Target({ TYPE, METHOD}) //该注解可用于类、接口和方法
public @interface NoVerifyToken {
	boolean VerifyRequired() default false;
}
