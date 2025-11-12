package com.zmy.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    public static <T> T deserialize(String jsonString, Class<T> valueType) throws JsonProcessingException {
        return objectMapper.readValue(jsonString, valueType);
    }
}
