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
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "Role")
@Table(name = "tb_role")
@Inheritance(strategy = InheritanceType.JOINED) // 继承映射，连接子类的映射策略
@DynamicUpdate //修改Role动态生成sql语句
@DynamicInsert //插入Role动态生成sql语句，解决not null default无法自动插入默认值的问题
public class Role {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "role_id")
	private Long id;
	// 手机号码（登录）
	@Column(name = "role_phone_number", length = 11)
	private String phoneNumber;
	// 电子邮箱（登录+密码找回）
	@Column(name = "role_email", nullable = false, length = 45)
	private String email;
	// 密码
	@Column(name = "role_password", nullable = false)
	private String password;
	// 昵称
	@Column(name = "role_nickname", nullable = false, length = 32)
	private String nickname;
	// 头像（存绝对路径）
	// 使用@Column(columnDefinition = "")需要自己设置数据类型
	@Column(name = "role_image", columnDefinition = "varchar(255) not null default '/images/head-portraits/admin.jpg'")
	private String image;
	// 姓名
	@Column(name = "role_name", length = 13)
	private String name;
	// 性别
	@Column(name = "role_sex", columnDefinition = "enum('男','女') not null default '男'")
	private String sex;
	// 出生日期
	@Column(name = "role_birthday", length = 10)
	private String birthday;
	// QQ
	@Column(name = "role_qq", length = 12)
	private String qq;
	// 类型（0管理员1教师2学生）
	@Column(name = "role_type", nullable = false, columnDefinition = "enum('0','1','2') not null default '2'")
	private String type;
	// 注册时间
	@Column(name = "role_reg_time", nullable = false, length = 19)
	private String regTime;// yyyy-MM-dd HH:mm:ss
	// 最近一次登入时间
	@Column(name = "role_last_log_in_time", length = 19)
	private String lastLogInTime;
	// 最近一次登出时间
	@Column(name = "role_last_log_out_time", length = 19)
	private String lastLogOutTime;
	// 最近一次登录IP或注册时IP
	@Column(name = "role_ip", length = 15)
	private String ip;
	//账号激活
	@Column(name = "role_activated", columnDefinition = "tinyint(1) not null default '0'")
	private Boolean activated;
	//冻结秒数
	@Column(name = "role_freeze_seconds", columnDefinition = "bigint not null default '0'")
	private Long freezeSeconds;

}
