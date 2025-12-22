package com.pear.shop.Item;

import com.pear.shop.comment.Comment;
import com.pear.shop.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Controller
@RequiredArgsConstructor
public class ItemController {

//    @Autowired
//    public ItemController(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }

    private final ItemRepository itemRepository;
    private final ItemService itemService;
    private final CommentService commentService;
//    private final S3Service s3Service;

    @GetMapping("/list")
    String list(Model model){

        // protected 버전 코드 근데 이건 null 출력 됨
//        var c = new Item();
//        System.out.println(c.title);

        // static 버전 코드 직접적으로 사용 가능함.
//        System.out.println(Item.title);

        // private 버전 코드
        // Item 엔티티에서 getter 설정해줘야
        // 데이터를 끌어쓸 수 있음
        // setter를 지정해주면 변수 수정할 수 있음

        List<Item> result = itemRepository.findAll();
        model.addAttribute("items", result);

        return "list";
    }

    @GetMapping("/list/page/{abc}")
    String getListPage(Model model, @PathVariable Integer abc){
        itemService.getPageList(model, abc);
        return "list";
    }

    // AWS s3에서 이미지 받아오는 메서드. 만약 properties에서 버킷 키를 입력했다면 주석 해제
//    @GetMapping("/presigned-url")
//    String getURL(@RequestParam String filename){
//        System.out.println(filename);
//        var result = s3Service.createPresignedUrl("test/" + filename);
//        System.out.println(result);
//        return result;
//    }


    @GetMapping("/write")
    String write(){
        return "write";
    }

    @GetMapping("/detail/{id}")
    String detail(@PathVariable Long id, @RequestParam(defaultValue = "1") Integer page, Model model) {
        // @ControllerAdvice 로 예외처리 방법
        // 자바 패키지로 Handler 생성 한 뒤 위 어노테이션 작성하면 된다.

            Page<Comment> comt = commentService.ListComment(id, page - 1);
            model.addAttribute("comments", comt);

            Item result = itemService.detailItem(id);
            model.addAttribute("item", result);
            return "detail";
    }



    @PostMapping("/add")
    String add(@RequestParam String title,
               @RequestParam Integer price,
               Authentication auth,
               @RequestParam(required = false) String imgUrl) {
        if(imgUrl == null || imgUrl.isEmpty()){
            imgUrl = "https://placehold.co/300";
        }
        itemService.saveItem(title, price, auth.getName(), imgUrl);
        return "redirect:/list/page/1";
    }

    // @ModelAttribute 사용
//@PostMapping("/add")
//String add(@ModelAttribute Item item){
//    System.out.println(item);
//    itemRepository.save(item);
//    return "redirect:/list";
//}

    // Map 자료구조 설명
//    @PostMapping("/add")
//    String add(@RequestParam Map formData){
//        System.out.println(formData);
//
//        Map<String, Object> test = new HashMap<>();
//        test.put("name","kim");
//        test.put("age",20);
//        System.out.println(test.get("name"));
//
//        return "redirect:/list";
//    }

    @GetMapping("/update/{id}")
    String update(@PathVariable Long id, Model model){
             Item item = itemService.detailItem(id);
        model.addAttribute("item",item);
        return "update.html";
    }

    @PostMapping("/setUpdate")
    String setUpdate(Long id, String newTitle, Integer newPrice){

            itemService.updateItem(id, newTitle, newPrice);
        return "redirect:/list";
    }

    @DeleteMapping("/item/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        itemService.deleteItem(id);
        return ResponseEntity.ok("삭제되었습니다.");
    }

//    @PostMapping("/search")
//    String search(@RequestParam String searchText, Model model){
//
//        var result = itemRepository.rawQuery1(searchText);
//        System.out.println(result);
//        model.addAttribute("items",result);
//
//        return "searchList";
//    }

    @GetMapping("/search")
    public String getSearch(@RequestParam String searchText,
                            @RequestParam(defaultValue = "0") Integer page,
                            Model model){

        Pageable pageable = PageRequest.of(page,5);

        Page<Item> result = itemRepository.searchByTitleOrPrice(searchText, pageable);

        model.addAttribute("paging", result);
        model.addAttribute("searchText", searchText);
        return "list";
    }
}
