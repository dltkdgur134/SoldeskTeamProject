package com.soldesk6F.ondal.user;

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
        User forPassCheckUser = customUserDetails.getUser();
        
        String email = forPassCheckUser.getEmail();


        String targetProvider = userRepository.findBySocialLoginProvider("waiting:" + forPassCheckUser.getSocialLoginProvider())
        		.map(User::getEmail).orElse("No Data");


        if ((!targetProvider.equals("No Data"))&&targetProvider.equals(forPassCheckUser.getSocialLoginProvider())) {

        	response.sendRedirect("/oauth/passCheck?email=" + URLEncoder.encode(email, "UTF-8"));
            return;
        }
        
        super.onAuthenticationSuccess(request, response, authentication);

    }
}