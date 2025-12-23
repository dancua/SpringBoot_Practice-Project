package com.pear.shop.Member;

public class MemberDto {
        public String username;
        public String displayName;
        public Long id;
        MemberDto(String a, String b){
            this(a, b, null);
        }
        MemberDto(String a, String b, Long id) {
            this.username = a;
            this.displayName = b;
            this.id = id;
        }
}
