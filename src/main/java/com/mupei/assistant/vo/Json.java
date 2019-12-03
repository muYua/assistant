package com.mupei.assistant.vo;

import java.util.ArrayList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Json {
	//状态
	private boolean success;
	//数据
//	private ArrayList<?> obj; //集合可以存储不同类型的数据，前端得到数组
	private Map<String, Object> obj; //前端得到键值对集合
	//Layui数据接口
	private Integer code; //接口状态
	private String msg; //提示文本
	private Long count; //数据长度
	private ArrayList<?> data; //数据列表
}