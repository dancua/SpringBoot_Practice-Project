package com.pear.shop.comment;

import com.pear.shop.Member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    String comment(@RequestParam String content,
                   @RequestParam Long parent,
                   Authentication auth) {

        CustomUser user = (CustomUser) auth.getPrincipal();

        // 필요한 데이터만 Service로 전달
        commentService.saveComment(content, parent, user.getUsername());

        return "redirect:/detail/" + parent;
    }
}
