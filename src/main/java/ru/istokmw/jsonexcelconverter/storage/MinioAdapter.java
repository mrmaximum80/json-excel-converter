package ru.istokmw.jsonexcelconverter.storage;

import org.springframework.http.MediaType;

import java.util.Map;

public interface MinioAdapter {
    void uploadImage(String objectName, byte[] data, MediaType contentType);
    String getPresignedUrl(String objectName);
    Map<String, byte[]> getStorage();
}
