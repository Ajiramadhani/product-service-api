package com.example.product.service.impl;

import com.example.product.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class RedisServiceImpl implements RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void setValue(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
            log.debug("Cache set for key: {}", key);
        } catch (Exception e) {
            log.error("Error setting cache for key: {}", key, e);
        }
    }

    @Override
    public void setValue(String key, Object value, long timeout, TimeUnit unit) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, unit);
            log.debug("Cache set for key: {} with timeout: {} {}", key, timeout, unit);
        } catch (Exception e) {
            log.error("Error setting cache for key: {}", key, e);
        }
    }

    @Override
    public Object getValue(String key) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                log.debug("Cache hit for key: {}", key);
            }
            return value;
        } catch (Exception e) {
            log.error("Error getting cache for key: {}", key, e);
            return null;
        }
    }

    @Override
    public void deleteValue(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Cache deleted for key : {}", key);
        } catch (Exception e) {
            log.error("Error deleting cache for key : {}", key, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deletePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys !=null && !keys.isEmpty()){
            redisTemplate.delete(keys);
        }
    }

    @Override
    public boolean hasKey(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.error("Error checking key existence: {}", key, e);
            return false;
        }
    }
}
