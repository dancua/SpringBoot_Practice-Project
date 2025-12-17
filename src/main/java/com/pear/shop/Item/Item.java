package com.pear.shop.Item;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@Getter
@Setter
@Table(indexes = @Index(columnList = "title", name="작명"))
public class Item {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private Integer price;
    private String displayName;
    private String imageURL;

    // object 변수 한번에 출력하는 방법 
    // 해당하는 데이터들을 지정해서 출력
//    public String toString() {
//        return this.title + this.price;
//    }

    public String getTitle(){
        return title;
    }


}

