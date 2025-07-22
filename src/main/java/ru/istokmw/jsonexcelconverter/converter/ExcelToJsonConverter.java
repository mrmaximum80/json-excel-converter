package ru.istokmw.jsonexcelconverter.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import ru.istokmw.jsonexcelconverter.model.Item;
import ru.istokmw.jsonexcelconverter.model.Parameter;
import ru.istokmw.jsonexcelconverter.model.Photo;
import ru.istokmw.jsonexcelconverter.service.ExcelParserService;
import ru.istokmw.jsonexcelconverter.service.ImageLoadService;
import ru.istokmw.jsonexcelconverter.service.JsonConverterService;
import ru.istokmw.jsonexcelconverter.service.MinioImageService;
import ru.istokmw.jsonexcelconverter.storage.MinioAdapter;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.istokmw.jsonexcelconverter.constants.ExcelConstants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelToJsonConverter {

    private final ExcelParserService excelParserService;
    private final JsonConverterService jsonConverterService;

    public String convert(Workbook excelFile) {
        Map<String, Item> items = excelParserService.parseExcel(excelFile);
        return jsonConverterService.convertToJson(items);
    }

}
