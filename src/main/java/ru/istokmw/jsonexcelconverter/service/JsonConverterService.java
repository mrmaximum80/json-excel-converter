package ru.istokmw.jsonexcelconverter.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.istokmw.jsonexcelconverter.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class JsonConverterService {

    private final ObjectMapper mapper;

    public String convertToJson(Map<String, Item> items) {
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(new ArrayList<>(items.values()));
        } catch (JsonProcessingException e) {
            log.error("Ошибка парсинга JSON: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
