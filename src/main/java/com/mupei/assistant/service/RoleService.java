package com.mupei.assistant.service;

import com.mupei.assistant.model.Role;

public interface RoleService {
	
	public Role login(String idNumber, Role role, Integer flag);
	
	public Boolean reg(Role role);

}
