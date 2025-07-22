package ru.istokmw.jsonexcelconverter.service;

import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ImageLoadService {

    private final Pattern URL_PATTERN = Pattern.compile(
            "(?:^|[\\s])(https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*)",
            Pattern.CASE_INSENSITIVE
    );

    public byte[] downloadImage(String imageUrl) throws IOException {

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        URL url;
        try {
            url = new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid image URL: " + imageUrl, e);
        }

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000); // 5 seconds
        connection.setReadTimeout(10000);   // 10 seconds

        // Скачиваем файл
        try (InputStream in = connection.getInputStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    public void saveImage(String destinationPath, String imageName, byte[] image) throws IOException {

        Path destination = Paths.get(destinationPath + imageName);
        Files.write(destination, image, StandardOpenOption.CREATE);

    }

    public List<String> extractUrls(String text) {
        List<String> urls = new ArrayList<>();
        Matcher matcher = URL_PATTERN.matcher(text);

        while (matcher.find()) {
            String url = matcher.group(1).trim();
            // Удаляем trailing-символы
            url = url.replaceAll("[,.;!?]+$", "");
            urls.add(url);
        }

        return urls;
    }

    public String getImageType(String url) {
        return url.substring(url.lastIndexOf('.') + 1).toLowerCase();
    }
}
