package com.pear.shop.Item;

import lombok.Data;

@Data
public class ItemDto {
    private Long id;
    private String title;
    private Integer price;
    private String displayName;
    private String imgUrl;
}
