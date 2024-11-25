package org.example.flowday.global.security.util.oauth2.service;

import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.oauth2.dto.*;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(MemberRepository memberRepository, JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.jwtUtil = jwtUtil;
    }
    //DefaultOAuth2UserService OAuth2UserService의 구현체

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.println(oAuth2User.getAttributes());

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;
        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("kakao")) {

            oAuth2Response = new KakaoResponse(oAuth2User.getAttributes());
        }else{
            return null;
        }
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Optional<Member> existData = memberRepository.findByLoginId(username);
        if (existData.isEmpty()) {

            Member userEntity = new Member();
            userEntity.setLoginId(username);
            userEntity.setRole(Role.ROLE_USER);
            userEntity.setRefreshToken(jwtUtil.createJwt(Map.of(
                            "category","RefreshToken",
                            "loginId",username,
                            "role", "ROLE_USER"),
                    60 * 60 * 1000L));


            memberRepository.save(userEntity);

        }
        else {

            existData.get().setLoginId(username);
            existData.get().setRefreshToken(jwtUtil.createJwt(Map.of(
                            "category","RefreshToken",
                            "loginId",username,
                            "role", "ROLE_USER"),
                    60 * 60 * 1000L));

            memberRepository.save(existData.get());
        }

        return new CustomOAuth2User(oAuth2Response);
    }
}
