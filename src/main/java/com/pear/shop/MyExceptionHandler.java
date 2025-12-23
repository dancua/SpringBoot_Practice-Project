package com.pear.shop;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handler1(){
        return ResponseEntity.status(500).body("서버측 에러 발생!");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handler(Exception e) {
        e.printStackTrace();

        // 3. 응답 바디에 실제 에러 메시지를 담아서 보냄
        // 4. 상태 코드를 500(서버 에러)으로 변경
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("에러 발생 : " + e.getMessage());
    }
}
