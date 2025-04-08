package com.soldesk6F.ondal.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class GuestIdInterceptor implements HandlerInterceptor {

    private static final String GUEST_ID_COOKIE_NAME = "guest_id";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String guestId = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (GUEST_ID_COOKIE_NAME.equals(cookie.getName())) {
                    guestId = cookie.getValue();
                    break;
                }
            }
        }

        if (guestId == null) {
            guestId = UUID.randomUUID().toString();

            Cookie guestCookie = new Cookie(GUEST_ID_COOKIE_NAME, guestId);
            guestCookie.setPath("/");
            guestCookie.setHttpOnly(true);
            guestCookie.setSecure(true);
            guestCookie.setMaxAge(60 * 60 * 24 * 30); // 30일
            response.addCookie(guestCookie);

            System.out.println("[GuestIdInterceptor] guest_id 생성됨: " + guestId);
        }

        // ✅ request에 저장해두기
        request.setAttribute("guest_id", guestId);

        return true;
    }
}
