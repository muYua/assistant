package com.mupei.assistant.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {
    // @Resource，按名字注入Bean组件：RedisTemplate<String, Object>，
    // @Atuowired，按类型只能注入RedisTemplate、RedisTemplate<String,String>或StringRedisTemplate
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private JsonUtil jsonUtil;

    public void set(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public void set(String key, Object object) throws JsonProcessingException {
        stringRedisTemplate.opsForValue().set(key, jsonUtil.stringify(object));
    }

    /**
     * 存入键值对key-value
     *
     * @param key
     * @param value   String类型的值
     * @param timeout 长整型的过期时间
     * @param unit    时间单位
     */
    public void set(String key, String value, Long timeout, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, Object object, Long timeout, TimeUnit unit) throws JsonProcessingException {
        stringRedisTemplate.opsForValue().set(key, jsonUtil.stringify(object), timeout, unit);
    }

    public String get(String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    /**
     * 根据key值得到value，返回自定义类型
     *
     * @param key
     * @param valueType 转换结果的类型
     * @param <T>       泛型
     * @return 返回传入的valueType类型
     * @throws IOException
     */
    public <T> T getByObject(String key, Class<T> valueType) throws IOException {
        String value = stringRedisTemplate.opsForValue().get(key);
        return jsonUtil.parse(value, valueType);
    }
}
