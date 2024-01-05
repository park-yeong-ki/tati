package com.ssafy.tati.util;

import com.ssafy.tati.auth.dto.SecurityUserDto;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
    public static Integer getMemberId() {
        return ((SecurityUserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getMemberId();
    }

    public static SecurityUserDto getMember() {
        return ((SecurityUserDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    }
}
