package ru.istokmw.jsonexcelconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.MediaType;

@Data
@AllArgsConstructor
public class Photo {

    private String id;
//    @JsonIgnore
//    private byte[] imageData;
    private String imageName;
    private MediaType imageType;
    private String imageLink;
    private String minioLink;
}
