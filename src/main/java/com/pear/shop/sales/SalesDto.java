package com.pear.shop.sales;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SalesDto{
    private Long id;
    private String itemName;
    private Integer price;
    private Integer count;
    private String username;
    private LocalDateTime created;

    public SalesDto(Sales sales){
        this.id = sales.getId();
        this.itemName = sales.getItemName();
        this.price = sales.getPrice();
        this.count = sales.getCount();
        this.created = sales.getCreated();

        if(sales.getMember() != null){
            this.username = sales.getMember().getUsername();
        }
    }
}
