package com.pear.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @GetMapping("/register")
    String register(){
        return "/member";
    }

    @PostMapping("/setRegister")
    String setRegister(@RequestParam String username,
                       @RequestParam String password,
                       @RequestParam String displayName,
                       Model model) throws Exception {
        try {
            memberService.addMember(username, password, displayName);
        } catch (Exception e){
            e.printStackTrace();
            model.addAttribute("errorMessage", e.getMessage());
            return "register";
        }
        return "redirect:/list";
    }

    @GetMapping("/login")
    public String login(){
        var result = memberRepository.findByUsername("test1");
        System.out.println(result.get().getDisplayName());
        return "login";
    }

    @GetMapping("/my-page")
    public String myPage(Authentication auth){
        System.out.println(auth.getAuthorities().contains(new SimpleGrantedAuthority("USER")));
        CustomUser result = (CustomUser) auth.getPrincipal();
        System.out.println(result.getDisplayName());
        return "mypage";
    }

    @GetMapping("/user/1")
    @ResponseBody
    public MemberDto getUser(){
        var a = memberRepository.findById(1L);
        var result = a.get();
        var data = new MemberDto(result.getUsername(), result.getDisplayName());
        return data;
    }

    @GetMapping("/v2/user/1")
    @ResponseBody
    public MemberDto getUser2(){
        var a = memberRepository.findById(1L);
        var result = a.get();
        var data = new MemberDto(result.getUsername(), result.getDisplayName(), result.getId());
        data.id = 1L;
        return data;
    }
}

class MemberDto {
    public String username;
    public String displayName;
    public Long id;
     MemberDto(String a, String b){
        this(a, b, null);
    }
     MemberDto(String a, String b, Long id){
        this.username = a;
        this.displayName = b;
        this.id = id;
    }

}