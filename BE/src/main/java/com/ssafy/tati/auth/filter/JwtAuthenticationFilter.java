package com.ssafy.tati.auth.filter;

import com.ssafy.tati.auth.JwtTokenizer;
import com.ssafy.tati.auth.dto.SecurityUserDto;
import com.ssafy.tati.entity.Member;
import com.ssafy.tati.repository.MemberRepository;
import io.jsonwebtoken.JwtException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenizer jwtTokenizer;
    private final MemberRepository memberRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // request Header에서 AccessToken, RefreshToken을 가져온다.
        String accessToken = request.getHeader("Authorization");
        String refreshToken = request.getHeader("RefreshToken");
        String requestURI = request.getRequestURI();

        if ((!Objects.isNull(accessToken) && accessToken.startsWith("Bearer ")) || requestURI.equals("/member/reissue")) {
            try {
                //엑세스토큰 재발급
                if (requestURI.equals("/member/reissue")) {
                    String rtkSubject = jwtTokenizer.getSubject(refreshToken);
                    log.info("rtkSubject: {}", rtkSubject);

                    registerAuthentication(rtkSubject);
                }
                else {
                    String atk = accessToken.substring(7);
                    log.info("atk: {}", atk);
                    String atkSubject = jwtTokenizer.getSubject(atk);
                    log.info("atkSubject: {}", atkSubject);

                    if (jwtTokenizer.isBlackList(atk)) {
                        throw new JwtException("유효하지 않은 AccessToken 입니다.");
                    }

                    registerAuthentication(atkSubject);
                }
            } catch (JwtException e) {
                request.setAttribute("exception", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

    private void registerAuthentication(String subject) {
        Member findMember = memberRepository.findById(Integer.valueOf(subject))
                .orElseThrow(IllegalStateException::new);

        // SecurityContext에 등록할 User 객체를 만들어준다.
        SecurityUserDto userDto = SecurityUserDto.builder()
                .memberId(findMember.getMemberId())
                .email(findMember.getEmail())
                .role("ROLE_".concat(findMember.getRole()))
                .memberName(findMember.getMemberName())
                .build();

        // SecurityContext에 인증 객체를 등록해준다.
        Authentication token = getAuthentication(userDto);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    private Authentication getAuthentication(SecurityUserDto member) {
        return new UsernamePasswordAuthenticationToken(member, "",
                List.of(new SimpleGrantedAuthority(member.getRole())));
    }
}
