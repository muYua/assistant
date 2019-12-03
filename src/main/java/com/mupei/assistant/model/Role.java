package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Role")
@Table(name = "tb_role")
@Inheritance(strategy = InheritanceType.JOINED) // 继承映射，连接子类的映射策略
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Integer id;
	// 手机号码（登录）
	@Column(name = "role_phoneNumber")
	private String phoneNumber;
	// 电子邮箱（登录+密码找回）
	@Column(name = "role_email", nullable = false)
	private String email;
	// 密码
	@Column(name = "role_password", nullable = false)
	private String password;
	// 昵称
	@Column(name = "role_nickname", nullable = false)
	private String nickname;
	// 头像（存绝对路径）
	@Column(name = "role_image")
	private String image;
	// 姓名
	@Column(name = "role_name")
	private String name;
	// 性别
	@Column(name = "role_sex")
	private String sex;
	// 出生日期
	@Column(name = "role_birthday")
	private String birthday;
	// QQ
	@Column(name = "role_qq")
	private String qq;
	// 类型（0管理员1教师2学生）
	@Column(name = "role_type", nullable = false)
	private Integer type;
	// 注册时间
	@Column(name = "role_regTime", nullable = false)
	private String regTime;// yyyy-MM-dd HH:mm:ss
	// 最近一次登入时间
	@Column(name = "role_lastLogInTime")
	private String lastLogInTime;
	// 最近一次登出时间
	@Column(name = "role_lastLogOutTime")
	private String lastLogOutTime;
	// 最近一次登录IP或注册时IP
	@Column(name = "role_ip")
	private String ip;

}
