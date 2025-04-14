package com.soldesk6F.ondal.login;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.entity.User.UserRole;
import com.soldesk6F.ondal.user.repository.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class SinkService {
	
	public SinkService(UserRepository userRepository , PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		
	}	
		private final PasswordEncoder passwordEncoder;
		private final UserRepository userRepository;
		
		@Transactional
		public boolean trySink(String id , String password,boolean overRideProfile ,HttpServletRequest request) {
			User user = userRepository.findByUserId(id)
				    .orElseThrow(() -> new UsernameNotFoundException("해당 유저 없음: id=" + id));
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			User nowSessionUser = ((CustomUserDetails) authentication.getPrincipal()).getUser();
			
			String nowSessionProvider = nowSessionUser.getSocialLoginProvider();
		
			CustomUserDetails userDetails = new CustomUserDetails(user,user.getUserRole());
			
			//TO DO 권한 컬럼 생기면 권한에 넣고 세션 바꿔주기
			if(user.getSocialLoginProvider().equals("waiting:"+nowSessionProvider)&&
				passwordEncoder.matches(password, user.getPassword())) {
				if(overRideProfile) {
					String nowSessionProfileImg = nowSessionUser.getUserProfilePath();
					String nowSessionNickName = nowSessionUser.getNickName();
					user.setNickName(nowSessionNickName);
					user.setUserProfilePath(nowSessionProfileImg);
					
				}
				SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails,
					null,userDetails.getAuthorities()));
				
				request.getSession().setAttribute(
			    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
			    SecurityContextHolder.getContext());
				
				userRepository.deleteBySocialLoginProvider(nowSessionProvider);
				user.setSocialLoginProvider(nowSessionProvider);
				System.out.println("새 소셜 대기용계정 삭제후 세션 변경 성공");
				return true;
			}else {
				System.out.println("비밀번호 틀림");

				return false;
			}
					
			
			
		}
	
	
	
	
	
	
}
