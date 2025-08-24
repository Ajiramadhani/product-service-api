package com.example.product.service;

import java.util.concurrent.TimeUnit;

public interface RedisService {
    void setValue(String key, Object value);
    void setValue(String key, Object value, long timeout, TimeUnit unit);
    Object getValue(String key);
    void deleteValue(String key);
    void deletePattern(String pattern);
    boolean hasKey(String key);
}
