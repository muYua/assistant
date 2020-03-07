package com.mupei.assistant.model;

import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tb_file")
@DynamicUpdate //修改，动态生成sql语句
@DynamicInsert //插入，动态生成sql语句，解决not null default无法自动插入默认值的问题
public class File {
	//文件id
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long fileId;
	//文件名
	@Column(nullable = false)
	private String fileName;
	//文件大小
	@Column(nullable = false)
	private Long fileSize;
	//文件路径
	@Column(nullable = false)
	private String filePath;
	//创建人
	@Column(nullable = false, length = 32)
	private String roleName;
	//创建时间
	@Column(nullable = false, length = 19)
	private String createTime;// yyyy-MM-dd HH:mm:ss
	//类别（1教师布置作业2学生已交作业3课堂文件）
	@Column(nullable = false, columnDefinition = "enum('1','2','3') not null default '2'")
	private String sort;
}
