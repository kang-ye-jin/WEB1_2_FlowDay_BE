package org.example.flowday.global.security.util.oauth2.dto;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class CustomOAuth2User implements OAuth2User {

    private final OAuth2Response oAuth2Response;

    public CustomOAuth2User(OAuth2Response oAuth2Response) {

        this.oAuth2Response = oAuth2Response;
    }

    @Override
    public Map<String, Object> getAttributes() {

        return Map.of(
                "provider", oAuth2Response.getProvider(),
                "providerId", oAuth2Response.getProviderId(),
                "name", oAuth2Response.getName()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> collection = new ArrayList<>();

        collection.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {

                return "ROLE_USER";
            }
        });

        return collection;
    }

    @Override
    public String getName() {

        return oAuth2Response.getName();
    }

    public String getUsername() {

        return oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
    }
}
