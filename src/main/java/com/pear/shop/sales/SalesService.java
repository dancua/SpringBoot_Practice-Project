package com.pear.shop.sales;

import com.pear.shop.Member.CustomUser;
import com.pear.shop.Member.Member;
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

    public void salesItem(String itemName, Integer price, Integer count, Authentication auth){
        var sales = new Sales();
        sales.setItemName(itemName);
        sales.setPrice(price);
        sales.setCount(count);
        CustomUser user = (CustomUser) auth.getPrincipal();

        var member = new Member();
        member.setId(user.id);
        sales.setMember(member);

        salesRepository.save(sales);
    }

    public void getSaleItem(){
        List<Sales> result = salesRepository.customFindAll();
        System.out.println(result);

        var salesDto = new SalesDto();
        salesDto.itemName = result.get(0).getItemName();
        salesDto.price = result.get(0).getPrice();
        salesDto.username = result.get(0).getMember().getUsername();
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
