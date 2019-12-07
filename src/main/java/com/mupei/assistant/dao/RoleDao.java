package com.mupei.assistant.dao;

import java.util.ArrayList;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mupei.assistant.model.Role;

import io.lettuce.core.dynamic.annotation.Param;

/**
 * @description Tips
 * @author MUYUA
 * @tips 不用加@Repository注解,CrudRepository已经是IOC容器组件了
 * @tips JPA可以自动语句查询派生关键词（findBy..And..）
 * @tips @Query(nativeQuery = true) // 执行原生SQL语句
 * @tips @Query("...#{page.pageSize}...") public Object findByPage(Page page); //取封装对象属性
 */
 
public interface RoleDao extends CrudRepository<Role, Integer> {
	
	@Query(value = "FROM #{#entityName} r WHERE r.email = ?1 AND r.password = ?2", nativeQuery = false)
	public Role findByEmailAndPassword(String idNumber, String password);

	@Query("FROM #{#entityName} r WHERE r.phoneNumber = :idNumber AND r.password = :password")
	public Role findByPhoneNumberAndPassword(@Param("idNumber") String idNumber, @Param("password") String password);

	public Role findByEmail(String email);

	/* 分页查询 */	
//	@Query(value = "SELECT r.* FROM tb_role r LIMIT :startNo,:pageSize", nativeQuery = true)
//	public ArrayList<Object[]> findByPage(@Param("startNo") Long startNo, @Param("pageSize") Integer pageSize);
	@Query("FROM #{#entityName}")
	public ArrayList<Role> findByPage(Pageable pageable);

	/* 关键词模糊查询 */
	//-->like %value%
//	public ArrayList<Role> findByEmailContaining(String value);
	//-->like value，value可以是包含%_的字符串
	public ArrayList<Role> findByEmailLike(String value, Pageable pageable);
	public ArrayList<Role> findByPhoneNumberLike(String value, Pageable pageable);
	public ArrayList<Role> findByQqLike(String value, Pageable pageable);
	public ArrayList<Role> findByNicknameLike(String value, Pageable pageable);
	public ArrayList<Role> findByNameLike(String value, Pageable pageable);
	public ArrayList<Role> findByType(Integer value, Pageable pageable);
	//总数计数
	public Long countByEmailContaining(String value, Sort sort);
	public Long countByPhoneNumberContaining(String value, Sort sort);
	public Long countByQqContaining(String value, Sort sort);
	public Long countByNicknameContaining(String value, Sort sort);
	public Long countByNameContaining(String value, Sort sort);
	public Long countByType(Integer value, Sort sort);
	
}
