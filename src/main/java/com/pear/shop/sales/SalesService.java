package com.pear.shop.sales;

import com.pear.shop.Member.CustomUser;
import com.pear.shop.Member.Member;
import com.pear.shop.Member.MemberRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SalesService {

    private final SalesRepository salesRepository;
    private final MemberRepository memberRepository;

    public void salesItem(String username, SalesDto dto){

        // member에 있는 사용자 가져오기
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 주문 생성
        Sales sales = new Sales();
        sales.setItemName(dto.getItemName());
        sales.setPrice(dto.getPrice());
        sales.setCount(dto.getCount());

        sales.setMember(member);
        salesRepository.save(sales);

    }

    public void getSaleItem(){
        List<Sales> result = salesRepository.customFindAll();
        System.out.println(result);

        var salesDto = new SalesDto();
        salesDto.setItemName(result.get(0).getItemName());
        salesDto.setPrice(result.get(0).getPrice());
        salesDto.setUsername(result.get(0).getMember().getUsername());
    }

    public List<Sales> getSalesList(Long memberId){
        return salesRepository.findAllByMemberIdOrderByIdDesc(memberId);
    }

    @Transactional
    public void saveSale(String itemName, Integer price, Integer count){
        Sales sale = new Sales();
        salesRepository.save(sale);
    }

}
