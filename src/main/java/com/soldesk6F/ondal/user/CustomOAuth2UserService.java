package com.soldesk6F.ondal.user;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.OwnerRepository;
import com.soldesk6F.ondal.user.repository.RiderRepository;
import com.soldesk6F.ondal.user.repository.UserRepository;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

	private final UserRepository userRepository;
	private final RiderRepository riderRepository;
	private final OwnerRepository ownerRepository;

	public CustomOAuth2UserService(UserRepository userRepository, RiderRepository riderRepository,
			OwnerRepository ownerRepository) {
		this.userRepository = userRepository;
		this.riderRepository = riderRepository;
		this.ownerRepository = ownerRepository;
	}

	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		Map<String, Object> attributes = oAuth2User.getAttributes();
		String email = (String) attributes.get("account_email");
		String name = (String) attributes.get("name");
		String id = (String) attributes.get("id");
		String profileImg = (String) attributes.get("profile_image");
		String tel = (String) attributes.get("phone_number");
		String nickName = (String) attributes.get("profile_nickname");

		String extension = profileImg.substring(profileImg.lastIndexOf(".") + 1);
		String profilePhotoName = profileImg.substring(profileImg.lastIndexOf("/") + 1);

		// attributes로부터 필요한 정보 추출해서 DB 저장 또는 업데이트
		User user = userRepository.findByUserId(email).map(entity -> entity.update(name))
				.orElseGet(() -> userRepository.save(User.builder().userId(email) // 소셜 이메일을 아이디로 쓴다고 가정
						.userName(name).socialLoginProvider(id).email(email).userProfile(profileImg)
						.nickName(nickName).userPhone(tel).build()));
		boolean rider = riderRepository.existsByUser_UserId(email);
		boolean owner = ownerRepository.existsByUser_UserId(email);

		if (rider) {
			if (owner) {
				return new CustomUserDetails(user, attributes, Role.ALL);

			} else {
				return new CustomUserDetails(user, attributes, Role.RIDER);
			}

		} else if (owner) {
			return new CustomUserDetails(user, attributes, Role.OWNER);
		} else {
			return new CustomUserDetails(user, attributes, Role.USER);
		}

	}

}
