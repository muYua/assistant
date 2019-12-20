package com.mupei.assistant.utils;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

@Component
public class CookieUtil {

	/**
	 * 设置Cookie
	 *
	 * @param key      Cookie的ID
	 * @param value    Cookie的值
	 * @param maxAge   生命周期，以秒为单位。
	 *                 maxAge值为正数，表示存储在客户端本地，以cookie文件形式保存，不论关闭浏览器或关闭电脑，直到时间到才会过期；
	 *                 maxAge值为负数，表示存储在浏览器内存里，只要关闭浏览器，此cookie就会消失。maxAge默认值为-1。
	 * @param response
	 * @return Boolean
	 */
	public Boolean setCookie(String key, String value, int maxAge, HttpServletResponse response) {
		Cookie cookie = new Cookie(key, value);

		cookie.setPath("/");
		if (maxAge > 0)
			cookie.setMaxAge(maxAge);
		response.addCookie(cookie);// 将Cookie返回给浏览器
		return null;
	}

	/**
	 * 获取Cookie
	 *
	 * @param key     Cookie的ID
	 * @param request
	 * @return Cookie
	 */
	public Cookie getCookie(String key, HttpServletRequest request) {
		Map<String, Cookie> cookieMap = getCookies(request);

		if (cookieMap.containsKey(key)) {
			Cookie cookie = (Cookie) cookieMap.get(key);
			return cookie;
		} else {
			return null;
		}
	}

	/**
	 * 将Cookie封装到Map集合中
	 *
	 * @param request
	 * @return Cookie的Map键值对集合
	 */
	private Map<String, Cookie> getCookies(HttpServletRequest request) {
		Map<String, Cookie> cookieMap = new HashMap<String, Cookie>();
		Cookie[] cookies = request.getCookies();

		if (null != cookies) {
			for (Cookie cookie : cookies) {// 遍历cookies数组
				cookieMap.put(cookie.getName(), cookie);
			}
		}
		return null;
	}
}
