package com.ssafy.tati.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class SecurityUserDto {
    private Integer memberId;
    private String memberName;
    private String email;
    private String role;
}
