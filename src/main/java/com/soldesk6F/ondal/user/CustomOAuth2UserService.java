package com.soldesk6F.ondal.user;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
		OAuth2User oAuth2User = delegate.loadUser(userRequest);

		Map<String, Object> attributes = oAuth2User.getAttributes();
//	        System.out.println(attributes);
		String email = (String) ((Map) attributes.get("kakao_account")).get("email");
//	        System.out.println(email);
		String name = (String) ((Map) attributes.get("kakao_account")).get("name");
//	        System.out.println(name);
		String provider = String.valueOf(attributes.get("id"));
//	        System.out.println(provider);
		Map<String, Object> profileMap = (Map) ((Map) attributes.get("kakao_account")).get("profile");
		String nickName = (String) profileMap.get("nickname");
		String userProfilepath = (String)profileMap.get("profile_image");
//		        String profileImg = (String) attributes.get("profile_image");
		String tel = "0" + ((String) ((Map) attributes.get("kakao_account")).get("phone_number")).split(" ")[1];
		

//		        String extension = profileImg.substring(profileImg.lastIndexOf(".") + 1);
//		        String profilePhotoName = profileImg.substring(profileImg.lastIndexOf("/")+1);

		// attributes로부터 필요한 정보 추출해서 DB 저장 또는 업데이트

		User user = userRepository.findBySocialLoginProvider(provider).map(entity -> {

			if (true) {
				entity.update(nickName, userProfilepath);
				
			}
			return entity;
			

		}).orElseGet(() -> { 
			Optional<User> targetUser = userRepository.findByEmail(email);
			
			return targetUser.map(existingUser -> {
				
				existingUser.updateProvider("waiting:"+provider);
				return userRepository.save(User.builder().userId("temp_" + UUID.randomUUID()) 
						.userName(name).socialLoginProvider(provider).email("waiting:"+email)
						.password("waiting").userAddress("test")
						.userProfilePath(userProfilepath)
//					                .userProfilePath(profileImg)
//					                .userProfileName(profilePhotoName)
//					                .userProfileExtension(extension)
						.nickName(nickName).userPhone("waiting"+provider.substring(3,6)).build()
						);
			}).orElseGet(()-> {
				Random rand = new Random();
				String randString = String.valueOf(rand.nextInt(1000000000));
				return userRepository.save(User.builder().userId(email) 
						.userName(name).socialLoginProvider(provider).email(email)
						.password(String.format("%012d",Integer.parseInt(randString)))
						.userAddress("test")
						.userProfilePath(userProfilepath)
//					                .userProfilePath(profileImg)
//					                .userProfileName(profilePhotoName)
//					                .userProfileExtension(extension)
						.nickName(nickName).userPhone(tel).build()
						);
				
				
			});
			
	
			});
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
