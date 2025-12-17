package com.pear.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

        private final MemberRepository memberRepository;
        private final PasswordEncoder passwordEncoder;

    public void addMember(String username,
                          String password,
                          String displayName) throws Exception{
        validateInput(username, password);
        var member = new Member();
        member.setUsername(username);

        var hashing = passwordEncoder.encode(password);
        member.setPassword(hashing);

        member.setDisplayName(displayName);
        memberRepository.save(member);
    }

    // Hashing 방법 : bcrypt 사용
//    String register(){
//        var result = new BCryptPasswordEncoder().encode("암호화할 문자");
//        System.out.println(result);
//        return "redirect:/list";
//    }

    private void validateInput(String username, String password) throws Exception{
        if(memberRepository.findByUsername(username).isPresent()){
            throw new Exception("이미 존재하는 아이디입니다.");
        }
        if(username.length() < 4 || password.length() < 4) {
            throw new Exception("너무 짧습니다. 4글자 이상으로 적어주세요!");
        }
    }
}
