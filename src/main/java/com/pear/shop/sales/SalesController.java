package com.pear.shop.sales;

import com.pear.shop.Member.CustomUser;
import com.pear.shop.Member.Member;
import com.pear.shop.Member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class SalesController {

    private final SalesService salesService;
    private final MemberRepository memberRepository;

    @PostMapping("/sale")
    public String sale(@RequestParam String title,
                       @RequestParam Integer price,
                       @RequestParam Integer count,
                       Authentication auth
                       ){
        salesService.salesItem(title, price, count, auth);
        return "redirect:/list/page/1";
    }

    @GetMapping("/sale/all")
    String getSaleAll(Model model, Authentication auth){
        salesService.getSaleItem();
        return "sales";
    }

    @GetMapping("/sales")
    public String salesList(Authentication auth, Model model){

        if(auth == null || !auth.isAuthenticated()){
            return "redirect:/login";
        }
        String username = auth.getName();

        Optional<Member> memberOp = memberRepository.findByUsername(username);

        Member loginMember = memberOp.get();

        List<Sales> sales = salesService.getSalesList(loginMember.getId());

        System.out.println(sales);
        model.addAttribute("salesList",sales);
        return "sales";
    }
}
