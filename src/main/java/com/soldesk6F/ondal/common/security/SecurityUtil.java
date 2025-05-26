package com.soldesk6F.ondal.common.security;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {

    private SecurityUtil() {}          // 인스턴스화 방지

    /** 현재 로그인한 사용자의 UUID(PK) 리턴 */
    public static UUID currentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return UUID.fromString(auth.getName());   // username에 UUID가 들어가 있으므로 안전
    }

    /** 로그인 ID(사용자 ID)도 필요하다면 */
    public static String currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return ((com.soldesk6F.ondal.login.CustomUserDetails) auth.getPrincipal()).getUserUuid().toString();
    }
}
