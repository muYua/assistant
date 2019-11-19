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
	@Column(name = "t_workNumber")
	private String workNumber;
	// 学历
	@Column(name = "t_education")
	private String education;
	// 学位
	@Column(name = "t_degree")
	private String degree;
	// 职称
	@Column(name = "t_title")
	private String title;
	// 毕业学校
	@Column(name = "t_School")
	private String tSchool;
	// 毕业专业
	@Column(name = "t_Major")
	private String tMajor;

}
