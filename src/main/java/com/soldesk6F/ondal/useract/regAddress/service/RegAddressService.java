package com.soldesk6F.ondal.useract.regAddress.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
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
    				.address(address)
    				.detailAddress(detailAddress)
    				.userAddressLatitude(latitudeDouble)
    				.userAddressLongitude(longitudeDouble)
    				.isUserSelectedAddress(false)
    				.build();
    		regAddressRepository.save(regAddress);
    		if (findUser.get().getUserSelectedAddress() == null) {
    			regAddress.updateDefaultAddress(true);
    			findUser.get().updateUserAddresses(regAddress);
    			cud.getUser().setUserSelectedAddress(regAddress);
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
    /**
     * 로그인된 사용자의 선택된 주소를 조회해 모델에 담기
     */
    @Transactional(readOnly = true)
    public String getRegAddress(CustomUserDetails cud,
                                RedirectAttributes rAttr,
                                Model model) {
        Optional<User> optUser = userRepository.findByUserId(cud.getUsername());

        if (optUser.isEmpty()) {
            rAttr.addFlashAttribute("result", 1);
            rAttr.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
            // 돌려보낼 URL (예: 로그인 페이지)
            return "redirect:/login";
        }

        User user = optUser.get();

        RegAddress selected = user.getUserSelectedAddress();
        List<RegAddress> addressList = (selected == null)
            ? Collections.emptyList()
            : List.of(selected);

        model.addAttribute("addressList", addressList);
        return null; // null 이면 원래 뷰를 그대로 렌더링
	   }

    /**
     * 컨트롤러에서 넘어온 새 기본 주소(엔티티)를 User에 설정하고
     * 이전 기본주소의 flag도 토글
     */
	@Transactional
	public String selectDefaultAddress(CustomUserDetails cud,
	                                   UUID newRegAddressId,
	                                   RedirectAttributes rAttr) {
	    Optional<User> optUser = userRepository.findByUserId(cud.getUsername());
	    if (optUser.isEmpty()) {
	        rAttr.addFlashAttribute("result", 1);
	        rAttr.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
	        return "redirect:/login";
	    }
	
	    User user = optUser.get();
	
	    // 이전 기본주소 끄기
	    RegAddress old = user.getUserSelectedAddress();
	    if (old != null) {
	        old.setUserSelectedAddress(false);
	        regAddressRepository.save(old);
	    }
	
	    // 새 기본주소 켜기
	    RegAddress newAddr = regAddressRepository.findById(newRegAddressId)
	        .orElseThrow(); // 실제로는 예외 처리 추가
	    newAddr.setUserSelectedAddress(true);
	    regAddressRepository.save(newAddr);
	
	    // User 엔티티 갱신
	    user.setUserSelectedAddress(newAddr);
	    userRepository.save(user);
	
	    return "redirect:/rider/home"; // 변경 후 돌아갈 페이지
	}
	
}
