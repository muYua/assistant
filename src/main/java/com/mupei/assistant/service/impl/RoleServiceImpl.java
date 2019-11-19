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
	 * 登录业务逻辑
	 * @author MUYUA
	 * @param idNumber 用户账号：电子邮箱或手机号码
	 * @param role 角色信息 
	 * @param flag 用于判别用户账号类型；0：电子邮箱，1：手机号码
	 * @return Boolean
	 */
	@Override
	public Role login(String idNumber, Role role, Integer flag) {
		Role role2 = new Role();
		if(flag==0) {
			role2 = roleDao.findByEmailAndPassword(idNumber,role.getPassword());
		}
		else {
			role2 = roleDao.findByPhoneNumberAndPassword(idNumber, role.getPassword());
		}
		if(StringUtils.isEmpty(role2)) {
			return role2;
		}
		else {
			role2.setLastLogInTime(role.getLastLogInTime());
			role2.setIp(role.getIp());
			roleDao.save(role2);
			return role2;
		}
	}
	
	/* 注册业务逻辑 */
	@Override
	public Boolean reg(Role role) {
		if(!StringUtils.isEmpty(role)) {
			roleDao.save(role);
			return true;
		}
		else {
			return false;
		}
	}

}
