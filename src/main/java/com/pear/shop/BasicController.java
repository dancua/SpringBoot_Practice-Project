package com.pear.shop;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;

@Controller
public class BasicController {
    @GetMapping("/")
    String hello(){
        return "index.html";
    }

    @GetMapping("/about")
    @ResponseBody
    String about(){
        return "어바웃 기능이였습니다.";
    }

    @GetMapping("/mypage")
    @ResponseBody
    String mypage(){
        return "마이페이지입니다.";
    }

    @GetMapping("/date")
    @ResponseBody
    String date(){
        return String.valueOf(LocalDate.now());
    }

}
