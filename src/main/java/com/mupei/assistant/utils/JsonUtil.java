package com.mupei.assistant.utils;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonUtil {
	@Autowired
	ObjectMapper objectMapper;

	// 将对象转换为JSON字符串
	public String stringify(Object val) throws JsonProcessingException {

		return objectMapper.writeValueAsString(val);
	}

	// 将JSON字符串转换为对象
	public <T> T parse(String json, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {

		return objectMapper.readValue(json, valueType);
	}

}
