package com.pear.shop.Member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
// implements : 선언한 클래스가 참고하는 클래스를 따라하고 있는지 확인하는 코드
    // 선언한 클래스를 만들 때 특정 함수를 참고하게 강요하고 싶을 때 사용함.
    // 그래서 이걸 사용하는 목적은 이 규칙에 맞게 코드를 작성해라 라는 용도로 사용됨.
// interface : 함수 정의만 넣는 class
        private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var result = memberRepository.findByUsername(username);

        if(result.isEmpty()){
           throw new UsernameNotFoundException("아이디를 다시 입력해주세요!");
        }

        var user = result.get();
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("USER"));
        var a = new CustomUser(user.getUsername(), user.getPassword(), authorities);
        a.displayName = user.getDisplayName();
        a.id = user.getId();
        return a;

    }
}

