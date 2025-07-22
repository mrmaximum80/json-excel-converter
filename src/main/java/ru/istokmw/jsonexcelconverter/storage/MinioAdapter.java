package ru.istokmw.jsonexcelconverter.storage;

import java.io.InputStream;
import java.util.Map;

public interface MinioAdapter {
    void uploadImage(String objectName, byte[] data, String contentType);
    String getPresignedUrl(String objectName);
    Map<String, byte[]> getStorage();
}
