package ru.istokmw.jsonexcelconverter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ru.istokmw.jsonexcelconverter.model.Item;
import ru.istokmw.jsonexcelconverter.model.Parameter;
import ru.istokmw.jsonexcelconverter.model.Photo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.istokmw.jsonexcelconverter.constants.ExcelConstants.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelParserService {

    @Value("${excel.systemsheet.name}")
    private String systemSheetName;

    private final ImageLoadService imageLoadService;
    private final MinioImageService minioImageService;

    public Map<String, Item> parseExcel(Workbook excelFile) {

        Map<String, Item> items = new HashMap<>();
        Sheet sheet = excelFile.getSheetAt(WORK_SHEET_INDEX);
        Sheet systemSheet = excelFile.getSheetAt(SYSTEM_SHEET_INDEX);

        if (systemSheet.getSheetName() == null || !systemSheet.getSheetName().equals(systemSheetName)) {
            log.error("System sheet does not exist");
            throw new IllegalArgumentException("System sheet does not exist");
        }

        for (int rowNum = FIRST_DATA_ROW_NUMBER; rowNum <= sheet.getLastRowNum(); rowNum++) {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }

            Item item = parseItemRow(row, systemSheet);
            if (item != null) {
                items.put(item.getItemNumber(), item);
            }
        }
        return items;
    }

    private Item parseItemRow(Row row, Sheet systemSheet) {
        Item item = new Item();
        int cellNum = 0;
        String itemNum = row.getCell(cellNum++).getStringCellValue();
        if (itemNum == null || itemNum.isEmpty()) {
            log.error("ItemNumber is empty in row: {}.", row.getRowNum());
            return null;
        }
        try{item.setItemNumber(itemNum);
            item.setItemName(row.getCell(cellNum++).getStringCellValue());
            item.setBrand(row.getCell(cellNum++).getStringCellValue());
            item.setPrice(new BigDecimal(row.getCell(cellNum++).getStringCellValue()));
            item.setQuantity(Long.parseLong(row.getCell(cellNum++).getStringCellValue()));
            item.setPhotoLink(row.getCell(cellNum++).getStringCellValue());
            item.setPhotos(processPhotos(item));
            item.setParameters(parseParameters(systemSheet, cellNum, row));
            return item;
        } catch (Exception e) {
            log.error("Item parsing error in row: {}. {}", row.getRowNum(), e.getMessage());
        }
        return null;
    }

    private List<Parameter> parseParameters(Sheet systemSheet, int cellNum, Row row) {
        List<Parameter> parameters = new ArrayList<>();
        Sheet sheet = row.getSheet();
        int rowNum = row.getRowNum();
        while (systemSheet.getRow(NAMES_OF_PARAM_ROW_NUMBER).getCell(cellNum) != null) {
            try {
                Parameter parameter = new Parameter();
                parameter.setCode(systemSheet.getRow(NAMES_OF_PARAM_ROW_NUMBER).getCell(cellNum).getStringCellValue());
                parameter.setName(sheet.getRow(NAMES_OF_PARAM_ROW_NUMBER).getCell(cellNum).getStringCellValue());
                parameter.setValue(sheet.getRow(rowNum).getCell(cellNum++).getStringCellValue());
                parameters.add(parameter);
            } catch (Exception e) {
                log.error("Parameter parsing error in row: {}, cell: {}. {}", rowNum, cellNum, e.getMessage());
            }
        }
        return parameters;
    }

    private List<Photo> processPhotos(Item item) {
        List<String> urls = imageLoadService.extractUrls(item.getPhotoLink());
        List<Photo> photos = new ArrayList<>();
        for (int i = 0; i < urls.size(); i++) {
            try {
                String imageUrl = urls.get(i);
                Pair<MediaType, byte[]> imageData = imageLoadService.downloadImage(imageUrl);
                String imageName = item.getItemNumber() + "_image_" + i;

                String minioUrl = minioImageService.uploadImageToMinio(
                        imageData.getSecond(),
                        imageName,
                        imageData.getFirst()
                );
                photos.add(new Photo(
                        item.getItemNumber(),
                        imageName,
                        imageData.getFirst(),
                        imageUrl,
                        minioUrl
                ));
            } catch (Exception e) {
                log.error("Error processing photo: {}", e.getMessage());
            }
        }
        return photos;
    }
}
