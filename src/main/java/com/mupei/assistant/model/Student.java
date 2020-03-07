package com.mupei.assistant.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor // 全参数构造方法
@NoArgsConstructor // 无参构造方法
@Entity
@Table(name = "tb_stu")
public class Student extends Role {
	// 学号
	@Column(name = "stu_number", length = 16)
	private String stuNumber;
	// 所在地
	@Column(name = "stu_address")
	private String address;
	// 学校
	@Column(name = "stu_school", length = 55)
	private String school;
	// 院系
	@Column(name = "stu_department", length = 20)
	private String department;
	// 专业
	@Column(name = "stu_major", length = 20)
	private String major;
	// 班级
	@Column(name = "stu_class", length = 28)
	private String stuClass;
	// 入学时间
	@Column(name = "stu_enrollment_time", length = 10)
	private String enrollmentTime;

}
