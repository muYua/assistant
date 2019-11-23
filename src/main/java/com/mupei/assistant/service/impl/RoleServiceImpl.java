package com.mupei.assistant.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mupei.assistant.dao.RoleDao;
import com.mupei.assistant.model.Role;
import com.mupei.assistant.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleDao roleDao;
	
	/**
	 * @description 登录业务逻辑
	 * @author MUYUA
	 * @param idNumber 用户账号：电子邮箱或手机号码
	 * @param role 角色信息 
	 * @param flag 用于判别用户账号类型；0：电子邮箱，1：手机号码
	 * @return Boolean
	 */
	@Override
	public Role login(String idNumber, Role role, Integer flag) {
		Role role2 = new Role();
		
		if(flag==0) { // 电子邮箱
			role2 = roleDao.findByEmailAndPassword(idNumber,role.getPassword());
		}
		else { // 手机号码
			role2 = roleDao.findByPhoneNumberAndPassword(idNumber, role.getPassword());
		}
		
		if(StringUtils.isEmpty(role2)) {
			return null;
		}
		else {
			role2.setLastLogInTime(role.getLastLogInTime());
			role2.setIp(role.getIp());
			roleDao.save(role2);
			
			return role2;
		}
	}
	
	/* 自动登录，更新Role信息 */
	@Override
	public Boolean autoLogin(Role role) {
		if(role == null)
			return false;
		
		if(roleDao.save(role) == null) {
			return false;
		}else {
			return true;
		}
	}
	
	/* 注册业务逻辑 */
	@Override
	public Boolean reg(Role role) {	
		if(!StringUtils.isEmpty(role)) {
			Role findByEmail = roleDao.findByEmail(role.getEmail());
			if(findByEmail == null) {
				roleDao.save(role);
				return true;
			}else {
				return false;
			}
		}
		else {
			return false;
		}
	}

}
