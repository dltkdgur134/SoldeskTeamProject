package com.soldesk6F.ondal.useract.regAddress.service;

import java.util.Collections;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.user.entity.User;
import com.soldesk6F.ondal.user.repository.UserRepository;
import com.soldesk6F.ondal.user.service.UserService;
import com.soldesk6F.ondal.useract.regAddress.DTO.RegAddressDTO;
import com.soldesk6F.ondal.useract.regAddress.entity.RegAddress;
import com.soldesk6F.ondal.useract.regAddress.repository.RegAddressRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegAddressService {
	
	 private final UserRepository userRepository;
	 private final RegAddressRepository regAddressRepository;

	 private final UserService userService;
	
	 // 유저 주소 등록
    @Transactional
    public boolean regAddress(CustomUserDetails userDetails,
    		RedirectAttributes redirectAttributes,
    		String address,
    		String detailAddress,
    		String latitude,
    		String longitude) {
    	try {
    		String userUUIDString = userDetails.getUser().getUserUuidAsString();
        	UUID userUuid = UUID.fromString(userUUIDString);
        	Optional<User> findUser = userRepository.findById(userUuid);
    		
    		if (findUser.isEmpty()) {
    			redirectAttributes.addFlashAttribute("result", 1);
    			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    			return false;
        	}
    		
    		User user = findUser.get();
    		
    		double latitudeDouble = Double.parseDouble(latitude);
    		double longitudeDouble = Double.parseDouble(longitude);
    		
    		RegAddress regAddress = RegAddress.builder()
    				.user(user)
    				.address(address)
    				.detailAddress(detailAddress)
    				.userAddressLatitude(latitudeDouble)
    				.userAddressLongitude(longitudeDouble)
    				.isUserSelectedAddress(false)
    				.build();
    		regAddressRepository.save(regAddress);
    		
    		if (user.getUserSelectedAddress() == null) {
    			regAddress.updateDefaultAddress(true);
    			regAddress.setCreatedDate(LocalDateTime.now());
    			user.updateUserSelectedAddress(regAddress);
    			userService.refreshUserAuthentication(user.getUserId());
    		}
    		
    		redirectAttributes.addFlashAttribute("result", 0);
    		redirectAttributes.addFlashAttribute("resultMsg", "주소 등록 완료!");
    		return true;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "주소 등록 실패");
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

	
    // 유저가 선택한 수정할 주소 반환
    @Transactional(readOnly = true)
    public RegAddress getRegAddress(CustomUserDetails userDetails,
    		UUID regAddressId,
    		RedirectAttributes redirectAttributes,
    		Model model) {
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	if (findUser.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");	
    	} 
    	
    	User user = findUser.get();
    	
    	String regAddressUUIDString = regAddressId.toString();
    	UUID regAddressUuid = UUID.fromString(regAddressUUIDString);
    	Optional<RegAddress> findAddress = regAddressRepository.findByRegAddressIdAndUser(regAddressUuid, user);
    	
    	if (findAddress.isEmpty()) {
    		redirectAttributes.addFlashAttribute("result", 1);
    		redirectAttributes.addFlashAttribute("resultMsg", "등록되지 않은 주소입니다.");	
    	}
    	
    	model.addAttribute("address", findAddress.get());
    	return findAddress.get();
    }
    
    // 유저가 등록한 모든 주소 반환
	@Transactional (readOnly = true)
	public Optional<List<RegAddress>> getAllRegAddress(CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUuid = UUID.fromString(userUUIDString);
		Optional<User> findUser = userRepository.findById(userUuid);
		
		if (findUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			return null;
		}
		
		User user = findUser.get();
		
		Optional<List<RegAddress>> addressList = regAddressRepository.findAllByUser(user);
		model.addAttribute("addressList", addressList.get());	
		return addressList;
	}
	
	// 유저 기본 주소 변경
	@Transactional
	public boolean selectDefaultAddress (
			CustomUserDetails userDetails,
			UUID regAddressId,
			RedirectAttributes redirectAttributes) {
		try {
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString); 
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
				return true;
			}
			
			User user = findUser.get();
			
			Optional<List<RegAddress>> addressList = regAddressRepository.findAllByUser(findUser.get());
			RegAddress defaultAddress = null;
			for (RegAddress address : addressList.get()) {
				if (address.isUserSelectedAddress() == true) {
					defaultAddress = address;
				}
			}
			String defaultAddressUUIDString = defaultAddress.getRegAddressUuidAsString();
			UUID defaultAddressUuid = UUID.fromString(defaultAddressUUIDString);
			
			String addressUUIDString = regAddressId.toString();
			UUID addressUuid = UUID.fromString(addressUUIDString);
			
			if (addressUuid == defaultAddressUuid) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "이미 기본주소로 등록되어 있습니다.");
				return false;
			} else {
				Optional<RegAddress> findAddress = regAddressRepository.findById(addressUuid);
				
				if (findAddress.isEmpty()) {
					redirectAttributes.addFlashAttribute("result", 1);
					redirectAttributes.addFlashAttribute("resultMsg", "등록되지 않은 주소입니다.");
					return false;
				}
				
				RegAddress regAddress = findAddress.get();
				
				defaultAddress.setUserSelectedAddress(false);
				regAddress.updateDefaultAddress(true);
				regAddress.setUpdatedDate(LocalDateTime.now());
				user.updateUserSelectedAddress(regAddress);
				userService.refreshUserAuthentication(user.getUserId());
				redirectAttributes.addFlashAttribute("result", 0);
				redirectAttributes.addFlashAttribute("resultMsg", "기본 주소로 설정되었습니다!");
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "기본 주소 설정에 실패했습니다.");
			return false;
		}
	}
	
	// 주소 삭제
	@Transactional
	public boolean deleteAddress(CustomUserDetails userDetails,
			UUID regAddressId) {
		try {
			String regAddressIdString = regAddressId.toString();
			UUID regAddressUuid = UUID.fromString(regAddressIdString);
			Optional<RegAddress> findAddress = regAddressRepository.findById(regAddressUuid);
			
			if (findAddress.isEmpty()) {
				return false;
			}
			
			RegAddress regAddress = findAddress.get();
			
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			
			if (findUser.isEmpty()) {
				return false;
			}
			
			User user = findUser.get();
			regAddressRepository.delete(regAddress);
			userService.refreshUserAuthentication(user.getUserId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// 주소 정보 수정
	@Transactional
	public boolean updateAddress(CustomUserDetails userDetails,
			RegAddressDTO regAddressDTO,
			RedirectAttributes redirectAttributes) {
		try {
			String regAddressIdString = regAddressDTO.getRegAddressUuidAsString();
			UUID regAddressUuid = UUID.fromString(regAddressIdString);
			Optional<RegAddress> findAddress = regAddressRepository.findById(regAddressUuid);
			
			if (findAddress.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "등록되지 않은 주소입니다.");
				return false;
			}
			
			RegAddress regAddress = findAddress.get();
			
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			if (findUser.isEmpty()) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 유저입니다.");
				return false;
			}
			
			User user = findUser.get();
			
			// DTO에 String으로 받아왔기 때문에 파싱 필요
			double latitudeDouble = Double.parseDouble(regAddressDTO.getUserAddressLatitude());
    		double longitudeDouble = Double.parseDouble(regAddressDTO.getUserAddressLongitude());
    		
			findAddress.get().updateRegAddress( 
					regAddressDTO.getAddress(), 
					regAddressDTO.getDetailAddress(), 
					latitudeDouble,
					longitudeDouble);
			regAddress.setUpdatedDate(LocalDateTime.now());
			userService.refreshUserAuthentication(user.getUserId());
			redirectAttributes.addFlashAttribute("result", 0);
			redirectAttributes.addFlashAttribute("resultMsg", "주소 정보가 변경되었습니다.");
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "주소 변경에 실패했습니다.");
			return false;
		}
	}
}
