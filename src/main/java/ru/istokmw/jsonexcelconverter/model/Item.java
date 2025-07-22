package ru.istokmw.jsonexcelconverter.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Item {

    private String itemNumber;
    private String itemName;
    private String brand;
    private BigDecimal price;
    private Long quantity;
    private String photoLink;

    private List<Photo> photos;

    private List<Parameter> parameters;

}
