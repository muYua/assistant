package com.mupei.assistant.dao;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Role;

import io.lettuce.core.dynamic.annotation.Param;

// @Repository,CrudRepository已经是IOC容器组件了
public interface RoleDao extends CrudRepository<Role, Integer> {

//	@Query(nativeQuery = true) // 执行原生SQL语句

//	public Role findByEmailAndPassword(String idNumber, String password); // JPA可以自动语句查询派生关键词（findBy..And..）
	
	@Query(value = "FROM Role WHERE role_email = ?1 AND role_password = ?2", nativeQuery = false)
	public Role findByEmailAndPassword(String idNumber, String password);

	@Query("FROM Role r WHERE r.phoneNumber = :idNumber AND r.password = :password")
	public Role findByPhoneNumberAndPassword(@Param("idNumber") String idNumber, @Param("password") String password);

	public Role findByEmail(String email);

}
