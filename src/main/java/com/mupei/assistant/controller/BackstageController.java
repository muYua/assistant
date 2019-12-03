package com.mupei.assistant.controller;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mupei.assistant.annotation.NoVerifyToken;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.BackstageService;
import com.mupei.assistant.utils.EncryptUtil;
import com.mupei.assistant.utils.IpAddressUtil;
import com.mupei.assistant.utils.JsonUtil;
import com.mupei.assistant.vo.Json;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@NoVerifyToken
//@Controller
//@ResponseBody
@RestController
@RequestMapping("/admin")
public class BackstageController<T> {
	@Autowired
	private BackstageService<T> backstageService;
	
	@Autowired
	private IpAddressUtil ipAddressUtil;
	@Autowired
	private EncryptUtil encryptUtil;
	@Autowired
	private JsonUtil jsonUtil;
	
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置日期格式
	
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@GetMapping("/get{entity}s")
	public Json getEntities(@PathVariable("entity") String entity, Class<T> clazz) {
		Json json = new Json();
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			break;

		default:
			break;
		}
		
		ArrayList<T> list = backstageService.getAll(clazz);	
		
		json.setCount((long) list.size());
		json.setData(list);
		json.setCode(0);
		json.setMsg("获取数据成功！");			
		json.setSuccess(true);
		
		return json;
	}
	
	/**
	 * @description 分页查询
	 * @param pageNo 当前页码数
	 * @param pageSize 每页显示数据条数
	 * @param entity POJO名
	 * @param clazz POJO类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@GetMapping("/get{entity}sByPage")
	public Json getEntitiesByPage(@RequestParam("page") Integer pageNo, @RequestParam("limit") Integer pageSize, @PathVariable("entity") String entity, Class<T> clazz) {
		Json json = new Json();
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			break;

		default:
			break;
		}
		
		//数据总条数
		Long pageTotal =backstageService.getCount(clazz);
		
		//数据分页查询起始序号
//		Long startNo = (long) ((pageNo-1)*pageSize);

		ArrayList<T> list = backstageService.findByPage(pageNo, pageSize, clazz);
		
		json.setCount(pageTotal);
		json.setData(list);
		json.setCode(0); //0为成功代号（Layui）
		json.setMsg("获取分页数据成功！");	
		json.setSuccess(true);
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@DeleteMapping("/delete{entity}")
	public Json deleteEntity(@RequestParam("id") Integer id, @PathVariable("entity") String entity, Class<T> clazz) {
		Json json = new Json();
		
		if(id == null) {
			json.setSuccess(false);
			return json;
		}
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			break;

		default:
			break;
		}
		
		backstageService.deleteEntity(id, clazz);
		
		json.setSuccess(true);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@DeleteMapping("/delete{entity}s")
	public Json deleteRoles(@RequestParam("idList") ArrayList<Integer> idList, @PathVariable("entity") String entity, Class<T> clazz) {
		Json json = new Json();
		
		if(idList == null) {
			json.setSuccess(false);
			return json;
		}
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			break;

		default:
			break;
		}
		
		backstageService.deleteEntities(idList, clazz);
		
		json.setSuccess(true);
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@PostMapping("/add{entity}")
	public Json addEntity(@RequestBody T t, @PathVariable("entity") String entity, Class<T> clazz, HttpServletRequest request) throws NoSuchAlgorithmException, IOException {
		Json json = new Json();
		
		if(t == null) {
			json.setSuccess(false);
			return json;
		}
		
		// 获取注册时间
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
		String regTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

		// 获取注册时IP
		String ipAddr = ipAddressUtil.getIpAddr(request);
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			
			//将接收到的JSON数据重新地正确地解析成对应的POJO
			String jsonString = jsonUtil.stringify(t);
			Role role = jsonUtil.parse(jsonString, Role.class);
			
			role.setRegTime(regTime);
			role.setIp(ipAddr);
			
			log.debug("时间---[ {} ]", regTime);
			log.debug("IP地址---[ {} ]", ipAddr);
			
			// 算法加密
			String password = role.getPassword();
			String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
			role.setPassword(sha256);
			
			log.debug("密码---[ {} ]", role.getPassword());
			
			Boolean isAdd = backstageService.addEntity((T) role, clazz);
			
			if(isAdd) {
				log.debug("Role[ {} ]---数据添加成功！", role);
				
				json.setSuccess(true);
			}else {
				json.setSuccess(false);
			}
			
			break;

		default:
			break;
		}
		
		return json;
	}
	
	@SuppressWarnings("unchecked")
	@NoVerifyToken
	@PutMapping("/update{entity}")
	public Json updateEntity(@RequestBody T t, @PathVariable("entity") String entity, Class<T> clazz, HttpServletRequest request) throws IOException, NoSuchAlgorithmException {
		Json json = new Json();
		
		if(t == null) {
			json.setSuccess(false);
			return json;
		}
		
		// 获取注册时间
		simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置北京时区
		String updateTime = simpleDateFormat.format(new Date()); // new Date()为获取当前系统时间

		// 获取注册时IP
		String ipAddr = ipAddressUtil.getIpAddr(request);
		
		switch (entity) {
		case "Role":
			clazz = (Class<T>) Role.class;
			
			//将接收到的JSON数据重新地正确地解析成对应的POJO
			String jsonString = jsonUtil.stringify(t);
			Role role = jsonUtil.parse(jsonString, Role.class);
			
			role.setIp(ipAddr);
			
			log.debug("修改时间---[ {} ]", updateTime);
			log.debug("IP地址---[ {} ]", ipAddr);
			
			Role lastRole =(Role) backstageService.getEntityById(role.getId(), (Class<T>) Role.class);
			
			role.setImage(lastRole.getImage());
			role.setRegTime(lastRole.getRegTime());
			role.setLastLogInTime(lastRole.getLastLogInTime());
			role.setLastLogOutTime(lastRole.getLastLogOutTime());
			
			String password = role.getPassword();
			//如果该Role密码进行修改过
			if(!StringUtils.isEmpty(password))
			{
				// 算法加密
				String sha256 = encryptUtil.encryptWithSHA(password, "SHA-256");
				role.setPassword(sha256);
			}else {
				role.setPassword(lastRole.getPassword());
			}
			
			Boolean isUpdate = backstageService.updateEntity((T) role, clazz);
			
			if(isUpdate) {
				log.debug("Role[ {} ]---数据修改成功！", role);
				
				json.setSuccess(true);
			}else {
				json.setSuccess(false);
			}
			
			break;

		default:
			break;
		}
		
		return json;		
	}
}
