package org.example.flowday.global.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.flowday.domain.member.entity.Member;
import org.example.flowday.domain.member.entity.Role;
import org.example.flowday.domain.member.repository.MemberRepository;
import org.example.flowday.global.security.util.JwtUtil;
import org.example.flowday.global.security.util.SecurityUser;
import org.example.flowday.global.security.util.oauth2.dto.CustomOAuth2User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository;

    public JwtFilter(final JwtUtil jwtUtil, final MemberRepository memberRepository) {
        this.jwtUtil = jwtUtil;
        this.memberRepository = memberRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken) {

            CustomOAuth2User principal = (CustomOAuth2User) oauth2AuthenticationToken.getPrincipal();
            String loginId = principal.getUsername();

            Long id = memberRepository.findIdByLoginId(loginId).get();
            String role = principal.getAuthorities().iterator().next().getAuthority();

            String token = jwtUtil.createJwt(
                    Map.of("category","accessToken",
                            "id",id,
                            "loginId",principal.getUsername(),
                            "role", role)
                    , 60 * 60 * 1000L);
            System.out.println(id);
            response.setHeader("Authorization", "Bearer " + token);

            return;
        }


        //request에서 Authorization 헤더를 찾음
        String authorization = request.getHeader("Authorization");

        //Authorization 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            filterChain.doFilter(request, response);

            //조건이 해당되면 메소드 종료 (필수)
            return;
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        System.out.println("authorization now");
        //토큰 소멸 시간 검증
        try {
            if(!jwtUtil.isExpired(token)) {
                Date expirationTime = jwtUtil.getExpiration(token);

                long remainingTime = expirationTime.getTime() - System.currentTimeMillis();
                long minutes = remainingTime / 60000;
                long seconds = (remainingTime % 60000) / 1000;

                String formattedTime = String.format("만료까지 남은 시간 : %02d:%02d", minutes, seconds);
                System.out.println(formattedTime);
            }
        }  catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println(e.getMessage());
            response.getWriter().print("Access Denied");
            return;
        }

        Long id = jwtUtil.getId(token);
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);
        String category = jwtUtil.getCategory(token);

        if (!category.equals("accessToken")) {
            System.out.println(category);

            //response body
            PrintWriter writer = response.getWriter();
            writer.print("invalid access token");

            //response status code
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        //userEntity를 생성하여 값 set
        Member member = new Member();
        member.setId(Long.valueOf(id));
        member.setLoginId(username);
        member.setPw("temppassword");
        member.setRole(Role.valueOf(role));

        //UserDetails에 회원 정보 객체 담기
        SecurityUser customUserDetails = new SecurityUser(member);

        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);

    }
}

