package com.soldesk6F.ondal.useract.regAddress.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;
import com.soldesk6F.ondal.useract.regAddress.repository.RegAddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegAddressService {
	
	 private final UserRepository userRepository;
	 private final RegAddressRepository regAddressRepository;
	
    @Transactional
    public boolean regAddress(CustomUserDetails cud,
    		RedirectAttributes rAttr,
    		String address,
    		String detailAddress,
    		String latitude,
    		String longitude) {
    	Optional<User> findUser = userRepository.findByUserId(cud.getUsername());
    	
    	try {
    		if (findUser.isEmpty()) {
        		rAttr.addFlashAttribute("result", 1);
    			rAttr.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    			return false;
        	}
    		double latitudeDouble = Double.parseDouble(latitude);
    		double longitudeDouble = Double.parseDouble(longitude);
    		//Address homeAddress = new Address(address, detailAddress, latitudeDouble, longitudeDouble);
    		
//    		findUser.get().updateHomeAddress(homeAddress);
//    		cud.getUser().setHomeAddress(homeAddress);
    		
    		RegAddress regAddress = RegAddress.builder()
    				.user(findUser.get())
    				.address(address)
    				.detailAddress(detailAddress)
    				.userAddressLatitude(latitudeDouble)
    				.userAddressLongitude(longitudeDouble)
    				.build();
    		regAddressRepository.save(regAddress);
    		if (findUser.get().getUserSelectedAddress() == null) {
    			findUser.get().updateUserSelectedAddress(regAddress);
    		}
    		rAttr.addFlashAttribute("result", 0);
    		rAttr.addFlashAttribute("resultMsg", "주소 등록 완료!");
    		return true;
		} catch (Exception e) {
			e.printStackTrace();
			rAttr.addFlashAttribute("result", 1);
    		rAttr.addFlashAttribute("resultMsg", "주소 등록 실패");
    		return false;
		}
    }
	
	
	
	
}
