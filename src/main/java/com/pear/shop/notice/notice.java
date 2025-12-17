package com.pear.shop.notice;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Getter
public class notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("글제목")
    @Setter
    private String title;

    @Comment("날짜")
    private LocalDateTime day;

    // Getter 선언하면 해당 코드를 private 이여도 다른 클래스에서
    // 다른 곳에서 사용할 수 있음
    // Getter를 지정하면 이런 코드가 자동 작성 됨.

//    public void getTitle(String title){
//        this.title = title;
//    }

    // 단, Getter 도 조심해야하는 부분이 있는데,
    // N + 1 결과값을 도출할 수 있고,
    // Getter로 조건을 검사하면 변경에 취약해진다.

    // 그래서 이에 대한 해결 방법은
    // 조회를 했으면 그 결과값을 return 을 사용해서 반환하게 하면 좋다.


    // Setter 지정하면 이런 코드가 자동 작성 됨.
    // 왜 이 방법을 고수하냐 하면
    // 변수명에 직접적으로 접근할 수도 있어 보안적으로 안좋고,
    // 민간함 변수라면 데이터 값이 변경될 때, 에러 유발을 할 수 있기에
    // Setter를 사용하는 것이다.

    // 그렇다고 Setter를 남발하면 안된다.
    // 왜냐하면 값을 변경한 의도를 모르기도 하고,
    // 객체의 일관성을 유지하기 어렵기 때문이다.

    // 이런 Setter 대신에 의도과 명확한 메서드를 사용하는 것이 좋다.
    // 하지만 Setter를 사용해야 하는 경우라면
    // 사용하되, 내부 로직을 통해 제한을 걸거나,
    // 변경했으면, 그에 대한 의도를 적어놓기로 한다.

//    public void setTitle(String title) {
//        this.title = title;
//    }
}
