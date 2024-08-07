package com.project.fstudy.common.cls;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
public class Mapper<T, V> {
    private final Class<V> targetType;

    public V map(T object) throws JsonProcessingException {
        String json = (new ObjectMapper()).writeValueAsString(object);
        V res = (new ObjectMapper()).readValue(json, targetType);
        return res;
    }
}
