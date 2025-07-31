package ru.istokmw.jsonexcelconverter.storage;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MockMinioAdapter implements MinioAdapter {
    private final Map<String, byte[]> storage = new LinkedHashMap<>();

    @Override
    public void uploadImage(String objectName, byte[] data, MediaType contentType) {
        storage.put(objectName, data);
    }

    @Override
    public String getPresignedUrl(String objectName) {
        return "https://mock-minio.example.com/" + objectName;
    }

    public boolean isImageStored(String objectName) {
        return storage.containsKey(objectName);
    }

    public byte[] getImageData(String objectName) {
        return storage.get(objectName);
    }

    public Map<String, byte[]> getStorage() {
        return storage;
    }
}
