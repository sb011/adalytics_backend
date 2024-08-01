package com.adalytics.adalytics_backend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
@Component
public class QueryHelper {

    private final ObjectMapper objectMapper;
    private final Map<String, String> queries;
    public static String filePath = "GoogleQueries/googleQueries.json";


    public QueryHelper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.queries = new HashMap<>();
        try {
            ClassPathResource resource = new ClassPathResource(filePath);
            this.queries.putAll(objectMapper.readValue(resource.getInputStream(), new TypeReference<Map<String, String>>() {}));
        } catch (Exception ex) {
            log.error("Exception file not found : ", ex);
        }
    }

    public String getQuery(String key) {
        return this.queries.get(key);
    }
}
