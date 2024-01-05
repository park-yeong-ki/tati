package com.ssafy.tati.auth.handler;

import com.ssafy.tati.auth.JwtTokenizer;
import com.ssafy.tati.auth.Token;
import com.ssafy.tati.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class MyAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenizer jwtTokenizer;

    @Value("${url.frontend}")
    private String frontend;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        log.info("onAuthenticationSuccess");

        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        // 사용자 이메일을 가져온다.
        String email = oAuth2User.getAttribute("email");
        // 서비스 제공 플랫폼(GOOGLE, KAKAO, NAVER)이 어디인지 가져온다.
        String provider = oAuth2User.getAttribute("provider");

        // 사용자 식별자를 가져온다.
        Integer memberId = oAuth2User.getAttribute("memberId");
        String memberName = oAuth2User.getAttribute("memberName");

        // OAuth2User로 부터 Role을 얻어온다.
        String role = oAuth2User.getAuthorities().stream().
                findFirst() // 첫번째 Role을 찾아온다.
                .orElseThrow(IllegalAccessError::new) // 존재하지 않을 시 예외를 던진다.
                .getAuthority(); // Role을 가져온다.

        log.info("email: {}, provider: {}, memberId: {}, memberName: {}, role: {}", email, provider, memberId, memberName, role);

        // 회원이 존재하면 jwt token 발행을 시작한다.
        Member member = new Member();
        member.setEmail(email);
        member.setMemberId(memberId);
        member.setMemberName(memberName);
        member.setRole(role);
        Token token = jwtTokenizer.createTokensByLogin(member);
        log.info("accessToken = {}, refreshToken = {}", token.getAccessToken(), token.getRefreshToken());

        // accessToken, refreshToken을 쿼리스트링에 담는 url을 만들어준다.
        String targetUrl = UriComponentsBuilder.fromUriString(frontend + "/LoginSuccess")
                .queryParam("accessToken", token.getAccessToken())
                .queryParam("refreshToken", token.getRefreshToken())
                .build()
                .encode(StandardCharsets.UTF_8)
                .toUriString();

        log.info("redirect: {}", targetUrl);
        // 로그인 확인 페이지로 리다이렉트 시킨다.
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

}