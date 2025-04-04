package com.soldesk6F.ondal.user;

import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
@Service
public class Custom_oAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
	   
    private final UserRepository userRepository;

	   public Custom_oAuth2UserService(UserRepository userRepository) {
	        this.userRepository = userRepository;
	    }
	   
	@Override	
	    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
	        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
	        OAuth2User oAuth2User = delegate.loadUser(userRequest);

	        Map<String, Object> attributes = oAuth2User.getAttributes();

	        // attributes로부터 필요한 정보 추출해서 DB 저장 또는 업데이트
	        User user = saveOrUpdate(attributes);

	        return new CustomUserDetails(user, attributes);
	    }
	
	 private User saveOrUpdate(Map<String, Object> attributes) {
	        // OAuthAttributes 유틸 클래스로부터 email, name 등을 추출해서 User 생성 또는 업데이트
	        String email = (String) attributes.get("email");
	        String name = (String) attributes.get("name");

	        return userRepository.findByEmail(email)
	            .map(entity -> entity.update(name))
	            .orElseGet(() -> userRepository.save(User.builder()
	                .userId(email) // 소셜 이메일을 아이디로 쓴다고 가정
	                .userName(name)
	                .role("ROLE_USER")
	                .build()));
	    }
	
}
