package ru.istokmw.jsonexcelconverter.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.istokmw.jsonexcelconverter.storage.MinioAdapter;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MinioImageService {

    private final MinioAdapter minioAdapter;

    public String uploadImageToMinio(byte[] imageData, String imageName, String contentType) {
        String objectName = generateObjectName(imageName);
        minioAdapter.uploadImage(objectName, imageData, contentType);
        return minioAdapter.getPresignedUrl(objectName);
    }

    private String generateObjectName(String imageName) {
        return "images_" + imageName;
    }
}
