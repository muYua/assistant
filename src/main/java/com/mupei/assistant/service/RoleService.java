package com.mupei.assistant.service;

import com.mupei.assistant.model.Role;

public interface RoleService {
	
	public Role login(String idNumber, Role role, Integer flag);
	
	public Boolean reg(Role role);
	//激活账号
	public Boolean activateEmail(String email);

	public Boolean resetPassword(String email, String password);

    public Boolean deleteRoleByEmail(String email);

	public Boolean isExistByEmail(String email);

	public Role findByEmail(String email);

    public Role findById(Long id);

	public Boolean logout(Long id, String time);
}
