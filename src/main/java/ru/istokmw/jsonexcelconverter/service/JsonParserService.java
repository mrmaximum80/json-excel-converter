package ru.istokmw.jsonexcelconverter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.istokmw.jsonexcelconverter.model.Parameter;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonParserService {

    private final ObjectMapper objectMapper;

    public List<Parameter> parseJson(String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            log.warn("Empty JSON data provided");
            return Collections.emptyList();
        }

        try {
            return objectMapper.readValue(jsonData, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            log.error("Failed to parse JSON: {}", e.getMessage(), e);
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}
