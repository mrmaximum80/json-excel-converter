package ru.istokmw.jsonexcelconverter;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.istokmw.jsonexcelconverter.converter.JsonToExcelConverter;
import ru.istokmw.jsonexcelconverter.model.Item;
import ru.istokmw.jsonexcelconverter.service.ExcelParserService;
import ru.istokmw.jsonexcelconverter.service.ImageLoadService;
import ru.istokmw.jsonexcelconverter.service.JsonConverterService;
import ru.istokmw.jsonexcelconverter.storage.MinioAdapter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@SpringBootTest
class JsonExcelConverterApplicationTests {

    @Autowired
    private JsonToExcelConverter converter;

    @Autowired
    private ExcelParserService excelParserService;

    @Autowired
    private JsonConverterService jsonConverterService;

    @Autowired
    private ImageLoadService imageLoadService;

    @Autowired
    private MinioAdapter minioAdapter;

    @Test
    void jsonToExcelConverterTest() throws IOException {
        String jsonData = Files.readString(Paths.get("src\\main\\resources\\input2.json"));

        Workbook workbook = converter.convertJsonToExcel(jsonData);
        FileOutputStream fos = new FileOutputStream("src\\main\\resources\\output.xlsx");

        // Сохранить файл
        workbook.write(fos);
        workbook.close();
    }

    @Test
    void excelToJsonConverterTest() throws IOException {
        String jsonData = Files.readString(Paths.get("src\\main\\resources\\input2.json"));

        FileInputStream fis = new FileInputStream("src\\main\\resources\\output2.xlsx");
        Workbook workbook = new XSSFWorkbook(fis);

        Map<String, Item> items = excelParserService.parseExcel(workbook);

        System.out.println(jsonConverterService.convertToJson(items));
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        System.out.println(minioAdapter.getStorage().keySet());
        System.out.println("++++++++++++++++++++++++++++++++++++++++");
        for (Map.Entry<String, byte[]> entry : minioAdapter.getStorage().entrySet()) {
            String name = entry.getKey();
            byte[] data = entry.getValue();
            try(FileOutputStream fos = new FileOutputStream("src\\main\\resources\\" + name + ".jpg")) {
                fos.write(data);
            }
        }

        for (Map.Entry<String, Item> entry : items.entrySet()) {
            System.out.println(entry.getValue());
        }

    }

    @Test
    void ImageDownloaderTest() throws IOException {
        String imageUrl = "https://i.pinimg.com/originals/c0/2d/11/c02d11b807f28927def41b6346cb6da0.jpg";
        String savePath = "src\\main\\resources\\image.jpg";

        imageLoadService.downloadImage(imageUrl);

        System.out.println("Изображение успешно скачано!");

    }

}
