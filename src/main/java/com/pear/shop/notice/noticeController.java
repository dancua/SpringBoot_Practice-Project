package com.pear.shop.notice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class noticeController {
    private final noticeRepository notiRepository;

    @GetMapping("/notice")
    String notice(Model model){
        List<notice> result = notiRepository.findAll();
        model.addAttribute("notices",result);
        var a = new notice();

//        System.out.println(a.getTitle());
//        a.setTitle("abc");

        return "notice.html";
    }

}
