package com.pear.shop.refresh;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    private String role;

    // 생성자 (회원가입 수행 시 사용)
    @Builder
    public User(String username, String password, String nickname, String role){
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

}