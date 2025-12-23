package com.pear.shop.refresh;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository) {
        return args -> {
            // DB에 'tester'가 없으면 새로 만듭니다.
            if (userRepository.findByUsername("tester").isEmpty()) {
                User user = new User();
                user.setUsername("tester");
                user.setPassword(new BCryptPasswordEncoder().encode("1234")); // 비밀번호 1234
                user.setNickname("테스터");
                user.setRole("ROLE_USER");

                userRepository.save(user); // DB에 저장!

                System.out.println("======================================");
                System.out.println("테스트용 계정 생성됨: ID=tester / PW=1234");
                System.out.println("======================================");
            }
        };
    }
}
