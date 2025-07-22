package ru.istokmw.jsonexcelconverter.model;

public enum ItemEnum {
    ITEM_NUMBER("Артикул"),
    ITEM_NAME("Название"),
    BRAND("Бренд"),
    PRICE("Цена"),
    QUANTITY("Количество"),
    PHOTO("Фото"),
//    PARAMETERS("Параметры")
    ;

    private final String title;

    ItemEnum(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
