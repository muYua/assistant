package com.mupei.assistant.model;

import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
//@AllArgsConstructor // 全参数构造方法
//@NoArgsConstructor // 无参构造方法
@Entity
@Table(name = "tb_admin")
public class Admin extends Role {

}
