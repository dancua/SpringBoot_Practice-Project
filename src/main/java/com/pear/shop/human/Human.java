package com.pear.shop.human;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Human {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    private String name;

    private Integer age = 0;

    public void 한살더하기() {
        if(this.age == null){
            this.age = 0;
        }
        this.age = this.age + 1;
    }

    public void 나이설정(Integer age){
        if(age >= 100 || age < 0){
            System.out.println("다시 입력해주세요.");
        } else if(this.age < 0) {
            System.out.println("설정 잘못했어요.");
        } else {
            this.age = age;
        }
    }
}
