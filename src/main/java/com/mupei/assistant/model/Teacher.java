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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_teacher")
public class Teacher extends Role {
	// 教工号
	@Column(name = "t_work_number", length = 16)
	private String workNumber;
	// 学历
	@Column(name = "t_education", length = 25)
	private String education;
	// 学位
	@Column(name = "t_degree", length = 25)
	private String degree;
	// 职称
	@Column(name = "t_title", length = 25)
	private String title;
	// 毕业学校
	@Column(name = "t_School", length = 55)
	private String tSchool;
	// 毕业专业
	@Column(name = "t_Major", length = 20)
	private String tMajor;

}