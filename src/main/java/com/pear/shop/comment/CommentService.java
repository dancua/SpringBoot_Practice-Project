package com.pear.shop.comment;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public void saveComment(String content,
                            Long parent,
                            String username) {

        // 유효성 검사 (빈 댓글 방지 등) 로직을 여기에 추가할 수 있음
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("내용이 없습니다.");
        }

        var data = new Comment();
        data.setContent(content);
        data.setUsername(username);
        data.setParentId(parent);

        commentRepository.save(data);
    }

    public Page<Comment> ListComment(Long parentId, int page){
        PageRequest pageable = PageRequest.of(page, 7, Sort.by(Sort.Direction.DESC, "id"));
        return commentRepository.findAllByParentId(parentId, pageable);
    }
}
