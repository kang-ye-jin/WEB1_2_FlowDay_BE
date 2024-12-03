package org.example.flowday.global.security.config;

import lombok.extern.slf4j.Slf4j;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.filter.JwtFilter;
import org.example.flowday.global.security.filter.LoginFilter;
import org.example.flowday.global.security.filter.LogoutFilter;
import org.example.flowday.global.security.handler.CustomAuthenticationFailureHandler;
import org.example.flowday.global.security.handler.CustomAuthenticationSuccessHandler;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.oauth2.service.CustomOAuth2UserService;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityConfig {

    //AuthenticationManager가 인자로 받을 AuthenticationConfiguraion 객체 생성자 주입
    private final AuthenticationConfiguration authenticationConfiguration;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final MemberRepository memberRepository;

    private final CustomAuthenticationFailureHandler authenticationFailureHandler;

    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    public SecurityConfig(
            AuthenticationConfiguration authenticationConfiguration,
            CustomOAuth2UserService customOAuth2UserService,
            MemberRepository memberRepository,
            CustomAuthenticationFailureHandler authenticationFailureHandler,
            CustomAuthenticationSuccessHandler authenticationSuccessHandler) {

        this.authenticationConfiguration = authenticationConfiguration;
        this.customOAuth2UserService = customOAuth2UserService;
        this.memberRepository = memberRepository;
        this.authenticationFailureHandler = authenticationFailureHandler;
        this.authenticationSuccessHandler = authenticationSuccessHandler;

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtUtil jwtUtil) throws Exception {

        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/h2-console/**"
                        ).disable());

        http
                .formLogin(AbstractHttpConfigurer::disable);

        http
                .httpBasic(AbstractHttpConfigurer::disable);

        http
                .logout(AbstractHttpConfigurer::disable);

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                PathRequest.toStaticResources().atCommonLocations(),
                                new AntPathRequestMatcher("/h2-console/**")
                        )
                        .permitAll()
                        .requestMatchers(
                                "/connect/websocket",
                                "/api/v1/members/login",
                                "/api/v1/members/register",
                                "/oauth2/**",
                                "/api/v1/members/refresh",
                                "/error?continue",
                                "/api/v1/members/findId",
                                "/api/v1/members/findPW",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/actuator/**"
                        ).permitAll()
                        .anyRequest().hasRole("USER"));

        http
                .addFilterAt(
                        new LoginFilter(
                                authenticationManager(authenticationConfiguration),
                                jwtUtil,
                                memberRepository),
                                        UsernamePasswordAuthenticationFilter.class)
        ;

        http
                .addFilterBefore(new JwtFilter(jwtUtil, memberRepository), OAuth2AuthorizationRequestRedirectFilter.class);

        http
                .addFilterAt(new LogoutFilter(jwtUtil, memberRepository), org.springframework.security.web.authentication.logout.LogoutFilter.class);

        http
                .oauth2Login((oauth2) -> oauth2
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(customOAuth2UserService))
                        .successHandler(authenticationSuccessHandler)
                        .failureHandler(authenticationFailureHandler));

        http
                .headers(
                        headers -> headers
                                .addHeaderWriter(
                                        new XFrameOptionsHeaderWriter(
                                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)
                                )
                );

        return http.build();
    }
}
