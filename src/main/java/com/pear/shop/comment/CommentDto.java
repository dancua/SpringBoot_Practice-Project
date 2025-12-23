package com.pear.shop.comment;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CommentDto {
    private String content;
    private Long parentId;
}
