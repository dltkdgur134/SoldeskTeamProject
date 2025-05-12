package com.soldesk6F.ondal.login;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public OAuth2LoginSuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        User NowSessionPassCheckUser = customUserDetails.getUser();
        
        System.out.println("석세스 핸들러 접근 성공");

        String targetProvider = userRepository.findBySocialLoginProvider("waiting:" + NowSessionPassCheckUser.getSocialLoginProvider())
        		.map(User::getSocialLoginProvider).orElse("No Data");
        String userId = userRepository.findBySocialLoginProvider("waiting:" + NowSessionPassCheckUser.getSocialLoginProvider())
        		.map(User::getUserId).orElse("No Data");
        


        if ((!targetProvider.equals("No Data")) && targetProvider.equals("waiting:"+NowSessionPassCheckUser.getSocialLoginProvider())) {
            System.out.println("소셜계정 연동대기상태 판정");
        	response.sendRedirect("/oauth/passCheck?userId=" + URLEncoder.encode(userId, "UTF-8"));
            return;
        }

        super.onAuthenticationSuccess(request, response, authentication);

    }
}