package com.mupei.assistant.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.RoleService;
import com.mupei.assistant.utils.EmailUtil;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.IpAddressUtil;
import com.mupei.assistant.utils.JWTUtil;
import com.mupei.assistant.vo.Json;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RoleController {
	@Autowired
	private RoleService roleService;
	
	@Autowired
	EmailUtil emailUtil;
	@Autowired
	IpAddressUtil ipAddressUtil;
	@Autowired
	EncryptUtil encryptUtil;
	@Autowired
	JWTUtil jwtUtil;
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
	
	//打开登录入口
	@NoVerifyToken(VerifyRequired = false)
	@RequestMapping({"/index","/loginIndex"})
	public String index() {
		return "index";
	}
	
	/**
	 * 登录
	 * @param idNumber 用户账号：电子邮箱或手机号码
	 * @param role 接收前端传入的role的属性值，例如用户的密码
	 * @param flag 判断用户账号类型；0：电子邮箱，1：手机号码
	 * @param session 登录时存储用户信息到Session域
	 * @param request 通过Request流获取用户IP地址信息
	 * @return JSON 返回JSON数据
	 */
	@NoVerifyToken
	@ResponseBody
	@GetMapping("/login")
	public Json login(@RequestParam("idNumber") String idNumber,Role role,@RequestParam("flag") Integer flag,HttpSession session,HttpServletRequest request) {
		Json json = new Json();
		try {
			// 获取登入时间
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
			String lastLogInTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间
			
			// 获取登录时IP
			String ipAddr = ipAddressUtil.getIpAddr(request);
			
			role.setLastLogInTime(lastLogInTime);
			role.setIp(ipAddr);
			
			// 算法加密
			String password = role.getPassword();
			String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
			role.setPassword(sha256);
			
			// 登录校验
			 Role loginRole = roleService.login(idNumber, role, flag);
			 
			 log.debug("进行登录校验，获取登录实体：[ {} ]", loginRole);
			
			if(!StringUtils.isEmpty(loginRole))
			{
				// 生成JWT令牌
				String iss, aud, sub;
				iss = "com.mupei.assistant"; // 签发者
				aud = "com.edu.abtc"; // 接收者
				sub = loginRole.getId().toString(); // 主题
				
				final int EXP_MINUTES = 60*24*1; // 过期时间
				
				String token = jwtUtil.generateToken(iss, aud, sub, EXP_MINUTES); // 生成Token
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("token", token);
				
				String keyOfAES = encryptUtil.getKeyOfAES(); // 生成16位AES十六进制密钥
				map.put("key", keyOfAES);
				
				map.put("vi", encryptUtil.getVI()); // 获取AES加密算法的初始向量
				
				json.setObj(map); // 将数据发往前端
				
				log.debug("生成JWT令牌[ {} ]", token);
				
				// 登录成功
				log.debug("用户[ {} ]---登录成功！",idNumber);
				log.debug("登入时间--- [ {} ]", lastLogInTime);
				log.debug("登入IP--- [ {} ]", ipAddr);
				
				session.setAttribute("loginUser", role);
				json.setSuccess(true);
			}
			else {
				// 登录失败
				log.debug("用户[ {} ]---登录失败！",idNumber);
				
				json.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			log.error("登录异常！");
			
			json.setSuccess(false);
		}
		return json;
	}

	// 注册
	@NoVerifyToken
	@ResponseBody
	@PostMapping("/reg")
	public Json reg(Role role,HttpServletRequest request) {
		Json json = new Json();
		try {
			// 获取注册时间
			simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
			String regTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间
			
			// 获取注册时IP
			String ipAddr = ipAddressUtil.getIpAddr(request);
			
			log.debug("注册ip---[ {} ]", ipAddr);
			
			role.setRegTime(regTime);
			role.setIp(ipAddr);
			
			// 算法加密
			String password = role.getPassword();
			String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
			role.setPassword(sha256);
			
			// 注册逻辑
			Boolean reg = roleService.reg(role);
			
			if(reg) {
				log.debug("用户[ {} ]---注册成功！", role.getEmail());
				log.debug("注册密码---[ {} ]", role.getPassword());
				log.debug("注册时间---[ {} ]", regTime);
				
				json.setSuccess(true);
			}
			else {
				log.debug("用户[ {} ]---注册失败！", role.getEmail());
				
				json.setSuccess(false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			
			log.error("注册异常！");
			
			json.setSuccess(false);
		}
		return json;
	}

	// 获得邮件验证码
	@NoVerifyToken
	@RequestMapping("/getVerifyCode")
    @ResponseBody
    public String getVerifyCode(String email){
        String verifyCode = String.valueOf(new Random().nextInt(899999) + 100000);
        String message = "您的注册验证码为："+verifyCode;
        try {
        	emailUtil.sendSimpleMail(email, "注册验证码", message);
        	
        	log.debug("邮件发往[ {} ]", email);
        }catch (Exception e){
        	e.printStackTrace();
            return "";
        }
        return verifyCode;
    }

//	@NoVerifyToken
	@PutMapping("/resetPassword")
    @ResponseBody
	public Json resetPassword(HttpServletRequest request) {
		Json json = new Json();
		json.setSuccess(true);
		return json;		
	}

}
