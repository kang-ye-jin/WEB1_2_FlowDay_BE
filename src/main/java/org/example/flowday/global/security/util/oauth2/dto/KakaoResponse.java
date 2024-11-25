package org.example.flowday.global.security.util.oauth2.dto;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attribute;

    public KakaoResponse(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProvider() {
        // 카카오에서 제공하는 프로바이더 이름을 반환
        return "kakao";
    }

    @Override
    public String getProviderId() {
        // 카카오 사용자 ID를 반환 (attribute에서 "id" 가져오기)
        return attribute.get("id").toString();
    }

    @Override
    public String getEmail() {
        // "kakao_account"에서 이메일 정보를 가져옴
        Map<String, Object> kakaoAccount = (Map<String, Object>) attribute.get("kakao_account");
        if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
            return kakaoAccount.get("email").toString();
        }
        return null; // 이메일이 없으면 null 반환
    }

    @Override
    public String getName() {
        // "properties"에서 닉네임 정보를 가져옴
        Map<String, Object> properties = (Map<String, Object>) attribute.get("properties");
        if (properties != null && properties.containsKey("nickname")) {
            return properties.get("nickname").toString();
        }
        return null; // 닉네임이 없으면 null 반환
    }
}

