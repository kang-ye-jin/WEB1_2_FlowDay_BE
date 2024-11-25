package org.example.flowday.global.security.util;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SecurityUserService implements UserDetailsService {

    private final MemberRepository memberRepository;

    public SecurityUserService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        System.out.println("Looking for user with loginId: " + loginId);
        Optional<Member> member = memberRepository.findByLoginId(loginId);
        if (member.isPresent()) {
            System.out.println("find successful");
            return new SecurityUser(member.get());
        } else {
            System.out.println("find failed");
            throw new UsernameNotFoundException("User not found with loginId: " + loginId);
        }
    }
}
