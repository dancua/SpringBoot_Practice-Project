package com.pear.shop.sales;

import com.pear.shop.Member.CustomUser;
import com.pear.shop.Member.Member;
import com.pear.shop.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;
    private final MemberRepository memberRepository;

    @PostMapping("/sale")
    public ResponseEntity<String> sale(@RequestBody SalesDto dto, Authentication auth){
        if(auth == null){
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }
        try{
            salesService.salesItem(auth.getName(), dto);
            return ResponseEntity.ok("주문 성공");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("주문 실패: " + e.getMessage());
        }
    }

    // 주문 내역 확인 테스트 기능
    @GetMapping("/sale/all")
    String getSaleAll(Model model, Authentication auth){
//        salesService.getSaleItem();
        var result = memberRepository.findById(1L);
        System.out.println(result.get().getSales());
        return "sales";
    }

    // 화면 보여주는 메서드
    @GetMapping("/sales")
    public String salesPage(){
        return "sales";
    }

    // 실제 데이터를 넘겨주는 api
    @GetMapping("/api/sales")
    @ResponseBody
    public ResponseEntity<Object> getMySales(Authentication auth){

        // auth 가 없으면 403(FORBIDDEN)
        if(auth == null || !auth.isAuthenticated()){
            return ResponseEntity.status(403).body("로그인이 필요합니다.");
        }

        // 유저 정보 가져오기
        CustomUser user = (CustomUser) auth.getPrincipal();
        String username = user.getUsername();

        // DB 조회
        Member member = memberRepository.findByUsername(username).get();

        List<Sales> salesEntities = salesService.getSalesList(member.getId());

        // dto 리스트로 변환
        List<SalesDto> dtos = salesEntities.stream()
                .map(SalesDto::new)
                .toList();

        return ResponseEntity.ok(dtos);

    }

}
