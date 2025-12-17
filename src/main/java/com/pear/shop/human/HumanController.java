package com.pear.shop.human;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
public class HumanController {

    @GetMapping("/human")
    @ResponseBody
    String human(){

        var object = new Human();
        object.나이설정(12);
        object.한살더하기();
        object.나이설정(14);

        System.out.println(object.getAge());
        return "저장 완료";
    }
}
