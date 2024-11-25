package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final Member member;

    public SecurityUser(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        System.out.println("Authorities Check");
        // 권한을 반환 (예: ROLE_USER 등)
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        System.out.println("Password Check");
        return member.getPw();
    }

    @Override
    public String getUsername() {
        System.out.println("Username Check");
        return member.getLoginId();
    }

    public Long getId(){
        System.out.println("Id Check");
        return member.getId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 조건에 따라 설정
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 조건에 따라 설정
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 조건에 따라 설정
    }

    @Override
    public boolean isEnabled() {
        return true; // 조건에 따라 설정
    }
}
