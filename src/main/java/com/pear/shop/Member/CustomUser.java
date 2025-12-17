package com.pear.shop.Member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class CustomUser extends User {

//    private static final long serialVersionID = 1L;
    public Long id;
    private String displayName;

    public CustomUser(String username,
                      String password,
                      Collection<? extends GrantedAuthority> authorities,
                      String displayName) {
        super(username, password, authorities);
        this.displayName = displayName;
        this.id = id;
    }
    public String getDisplayName(){
        return displayName;
    }
}
