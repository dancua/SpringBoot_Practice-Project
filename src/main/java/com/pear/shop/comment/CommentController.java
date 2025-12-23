package com.pear.shop.comment;

import com.pear.shop.Member.CustomUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/comment")
    @ResponseBody
    public ResponseEntity<String> comment(@RequestBody CommentDto dto,
                                  Authentication auth) {

        if(auth == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다");
        }
        // 필요한 데이터만 Service로 전달
        commentService.saveComment(dto.getContent(), dto.getParentId(), auth.getName());

        return ResponseEntity.ok("댓글 등록 성공");
    }
}
