package ru.istokmw.jsonexcelconverter.converter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.istokmw.jsonexcelconverter.model.ItemEnum;
import ru.istokmw.jsonexcelconverter.model.Parameter;
import ru.istokmw.jsonexcelconverter.service.JsonParserService;
import ru.istokmw.jsonexcelconverter.style.ExcelStyleFactory;

import java.util.List;

import static ru.istokmw.jsonexcelconverter.constants.ExcelConstants.COLUMN_WIDTH_MULTIPLIER;
import static ru.istokmw.jsonexcelconverter.constants.ExcelConstants.MAX_ROWS;


@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelTemplateGenerator {

    @Value("${excel.sheet.password}")
    private String excelPassword;

    @Value("${excel.sheet.name}")
    private String sheetName;

    @Value("${excel.systemsheet.name}")
    private String systemSheetName;

    private final JsonParserService jsonParser;


    public Workbook generateExcelTemplate(String jsonData) {

        List<Parameter> parameters = jsonParser.parseJson(jsonData);
        log.debug("Parsed {} parameters", parameters.size());

        System.out.println(parameters);

        // Создать новую книгу Excel
        Workbook workbook = new XSSFWorkbook();
        ExcelStyleFactory styleFactory = new ExcelStyleFactory(workbook);

        Sheet sheet = createMainSheet(workbook);
        Sheet systemSheet = createSystemSheet(workbook);

        System.out.println(excelPassword);

        // Создать стиль для заголовков
        CellStyle headerStyle = styleFactory.createHeaderStyle();

        // Создать стиль для данных для текста
        CellStyle textCellStyle = styleFactory.createTextStyle();

        // Заполнить заголовки
        fillHeaders(sheet,headerStyle,parameters);

        fillParameters(sheet, systemSheet, parameters, headerStyle);

        // Автоподбор ширины столбцов
        adjustColumnWidth(sheet, parameters);

        // Подготовить строки для данных
        prepareDataRows(sheet, parameters, textCellStyle);

        return workbook;
    }

    private void prepareDataRows(Sheet sheet, List<Parameter> parameters, CellStyle textCellStyle) {
        for (int rowNum = 2; rowNum <= MAX_ROWS; rowNum++) {
            Row row = sheet.createRow(rowNum);
            for (int colNum = 0; colNum < parameters.size() + ItemEnum.values().length; colNum++) {
                Cell cell = row.createCell(colNum);
                cell.setCellStyle(textCellStyle);
            }
        }
    }

    private void fillHeaders(Sheet sheet, CellStyle headerStyle, List<Parameter> parameters) {
        // Записать заголовки (первая и вторая строка)
        Row headerRow = sheet.createRow(0);
        Row secondHeaderRow = sheet.createRow(1);

        // Статичная часть из ItemEnum с попарным объединением ячеек по вертикали
        fillStaticHeaders(headerStyle, headerRow, secondHeaderRow);

        // Динамическая часть из списка параметров
        fillDynamicHeaders(sheet, headerStyle, parameters, headerRow);
    }

    private void fillStaticHeaders(CellStyle headerStyle, Row headerRow, Row secondHeaderRow) {
        int colNum = 0;
        for (ItemEnum itemEnum : ItemEnum.values()) {
            Cell headerCell = headerRow.createCell(colNum);
            Cell secondHeaderCell = secondHeaderRow.createCell(colNum++);
            headerCell.setCellValue(itemEnum.getTitle());
            headerCell.setCellStyle(headerStyle);
            secondHeaderCell.setCellStyle(headerStyle);
        }
    }

    private void fillDynamicHeaders(Sheet sheet, CellStyle headerStyle, List<Parameter> parameters, Row headerRow) {
        int startColNum = ItemEnum.values().length;
        int endColNum = startColNum + parameters.size() - 1;
        sheet.addMergedRegion(new CellRangeAddress(0, 0, startColNum, endColNum));

        for (int i = startColNum; i < endColNum; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellStyle(headerStyle);
        }

        Cell cell = headerRow.getCell(startColNum);
        cell.setCellValue("Параметры");
    }

    private void fillParameters(Sheet sheet, Sheet systemSheet, List<Parameter> parameters, CellStyle headerStyle) {

        int rowNum = 1;
        int colNum = ItemEnum.values().length;

        Row systemRow = systemSheet.createRow(rowNum);
        Cell systemCell;
        Cell cell;

        for (Parameter parameter : parameters) {
            Row row = sheet.getRow(rowNum);
            cell = row.createCell(colNum);
            String name = parameter.getName();
            cell.setCellValue(name);
            cell.setCellStyle(headerStyle);
            systemRow = systemSheet.getRow(rowNum);
            systemCell = systemRow.createCell(colNum++);
            String code = parameter.getCode();
            systemCell.setCellValue(code);
        }
    }

    private static void adjustColumnWidth(Sheet sheet, List<Parameter> parameters) {
        int totalColumns = ItemEnum.values().length + parameters.size();

        for (int i = 0; i < totalColumns; i++) {
            sheet.autoSizeColumn(i);
            int currentWidth = sheet.getColumnWidth(i);
            System.out.println(currentWidth);
            sheet.setColumnWidth(i, (int) (currentWidth * COLUMN_WIDTH_MULTIPLIER)); // +30% для кириллицы
        }

        // Объединение ячеек статических заголовков по вертикали после автоподбора ширины (если сделать до, автоподбор не работает)
        for (int i = 0; i < ItemEnum.values().length; i++) {
            sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
        }
    }

    private Sheet createMainSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet(sheetName);
        sheet.protectSheet(excelPassword); // Ставим защиту на лист
        XSSFSheet xssfSheet = (XSSFSheet) sheet;
        xssfSheet.lockFormatColumns(false);
        xssfSheet.lockFormatRows(false);
        return sheet;
    }

    private Sheet createSystemSheet(Workbook workbook) {
        Sheet systemSheet = workbook.createSheet(systemSheetName);
        workbook.setSheetVisibility(workbook.getSheetIndex(systemSheet), SheetVisibility.VERY_HIDDEN);
        return systemSheet;
    }

}
