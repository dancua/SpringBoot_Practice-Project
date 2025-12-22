package com.pear.shop.sales;

import com.pear.shop.Item.Item;
import com.pear.shop.Item.ItemRepository;
import com.pear.shop.Member.CustomUser;
import com.pear.shop.Member.Member;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SalesService {

    private final SalesRepository salesRepository;
    private final ItemRepository itemRepository;

    @Transactional
    public void salesItem(Long itemId, Integer count, Authentication auth){
        var sales = new Sales();
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 존재하지 않음"));

        // 주문한 수량만큼 item 테이블에 존재하는 count 컬럼(재고)가 차감됨.
        int orderCount = (count != null) ? count : 0;
        if(orderCount <= 0) {
            throw new IllegalArgumentException("1개 이상으로 적어주세요");
        }
        if(item.getCount() < orderCount){
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        item.setCount(item.getCount() - orderCount);

        // DB에 값 저장
        sales.setItemName(item.getTitle());
        sales.setPrice(item.getPrice());
        sales.setCount(orderCount);

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
