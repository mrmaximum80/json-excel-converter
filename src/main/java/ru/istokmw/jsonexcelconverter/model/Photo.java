package ru.istokmw.jsonexcelconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Photo {

    private String id;
//    @JsonIgnore
//    private byte[] imageData;
    private String imageName;
    private String imageType;
    private String imageLink;
    private String minioLink;
}
