package ru.istokmw.jsonexcelconverter.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageLoadService {

    private final Pattern URL_PATTERN = Pattern.compile(
            "(?:^|[\\s])(https?://(?:www\\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b[-a-zA-Z0-9()@:%_+.~#?&/=]*)",
            Pattern.CASE_INSENSITIVE
    );

    private final WebClient webClient;

    public byte[] downloadImage(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty");
        }

        try {
            return webClient.get()
                    .uri(imageUrl)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, response ->
                            Mono.error(new IOException("HTTP error: " + response.statusCode().value())))
                    .bodyToMono(byte[].class)
                    .block();
        } catch (RuntimeException  e) {
            if (e.getCause() instanceof DataBufferLimitException) {
                throw new IOException("Файл слишком большой. Увеличьте maxInMemorySize в WebClient", e.getCause());
            }
            throw new IOException("Ошибка загрузки изображения", e);
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
