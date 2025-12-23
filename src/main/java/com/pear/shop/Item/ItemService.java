package com.pear.shop.Item;

import com.pear.shop.Member.Member;
import com.pear.shop.Member.MemberRepository;
import com.pear.shop.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;

    public void saveItem(String title, Integer price, String image, String username){

        // 사용자가 지정한 닉네임은 연관관계 매핑을 통해 가져오기
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("로그인 정보가 없습니다."));

        var item = new Item();
        item.setTitle(title);
        item.setPrice(price);
        item.setImageURL(String.valueOf(image));
        item.setDisplayName(member.getDisplayName());
        itemRepository.save(item);
    }
    // fetch 를 이용한 ajax 방식
//    @PostMapping("/test1")
//    String test1(@RequestBody Map<String, Object> body){
//        System.out.println(body.get("name"));
//        return "redirect:/list";
//    }

    // fetch query String( test2?이름=값&이름2=값2 등) 을 이용한 ajax 방식
//    @GetMapping("/test2")
//    String test2(@RequestParam String name, @RequestParam Integer age){
//        System.out.println(name + " " + age);
//        System.out.println(age);
//        return "redirect:/list";
//    }

    public Item detailItem(@PathVariable Long id){
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이템이 없습니다. id=" + id));

        // try/catch 로 예외처리 하는 방식
//        try {
//            Optional<Item> result = itemRepository.findById(id);
//            if (result.isPresent()) {
//                model.addAttribute("item", result.get());
//                return "detail.html";
//            } else {
//                return "redirect:/list";
//            }
//        } catch (Exception e) {
//            System.out.println(e.getMessage());
////            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("데이터를 잘못 보냈어요!");
//        }
    }
    public void updateItem(Long id, String newTitle, Integer newPrice){
        if (newTitle == null || newTitle.length() > 100) {
            throw new IllegalArgumentException("제목이 너무 깁니다. (100자 이하)");
        }
        if (newPrice == null || newPrice < 0) {
            throw new IllegalArgumentException("가격은 음수가 될 수 없습니다.");
        }

        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이템입니다. id=" + id));
        item.setTitle(newTitle);
        item.setPrice(newPrice);
        itemRepository.save(item);
    }

    public void deleteItem(Long id){
        itemRepository.deleteById(id);
    }

    public void getPageList(Model model, @PathVariable Integer abc){
        Page<Item> result = itemRepository.findPageBy(PageRequest.of(abc-1,5));
        List<Item> items = itemRepository.findAll();

//        System.out.println(result.getTotalPages());
//        System.out.println(result.hasNext());
        model.addAttribute("paging", result);
        model.addAttribute("limit", result.hasNext());
        model.addAttribute("items",items);
    }


    public void checkOwner(Long id, String username) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        System.out.println(item.getDisplayName());
        System.out.println(username);

        // 작성자 비교하는 코드
        if(!item.getDisplayName().equals(username)){
            throw new IllegalStateException("이 글의 작성자가 아닙니다.");
        }
    }
}
