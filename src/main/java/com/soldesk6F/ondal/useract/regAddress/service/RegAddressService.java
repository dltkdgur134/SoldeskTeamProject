package com.soldesk6F.ondal.useract.regAddress.service;

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
//    	Optional<User> findUser = userRepository.findByUserId(cud.getUsername());
    	String userUUIDString = userDetails.getUser().getUserUuidAsString();
    	UUID userUuid = UUID.fromString(userUUIDString);
    	Optional<User> findUser = userRepository.findById(userUuid);
    	
    	try {
    		if (findUser.isEmpty()) {
    			redirectAttributes.addFlashAttribute("result", 1);
    			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
    			return false;
        	}
    		double latitudeDouble = Double.parseDouble(latitude);
    		double longitudeDouble = Double.parseDouble(longitude);
    		
    		RegAddress regAddress = RegAddress.builder()
    				.user(findUser.get())
    				.address(address)
    				.detailAddress(detailAddress)
    				.userAddressLatitude(latitudeDouble)
    				.userAddressLongitude(longitudeDouble)
    				.isUserSelectedAddress(false)
    				.build();
    		regAddressRepository.save(regAddress);
    		if (findUser.get().getUserSelectedAddress() == null) {
    			regAddress.updateDefaultAddress(true);
    			regAddress.setCreatedDate(LocalDateTime.now());
    			findUser.get().updateUserSelectedAddress(regAddress);
//    			cud.getUser().setUserSelectedAddress(regAddress);
    			userService.refreshUserAuthentication(findUser.get().getUserId());
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
	
    // 유저가 등록한 모든 주소 반환
	@Transactional (readOnly = true)
	public Optional<List<RegAddress>> getRegAddress(CustomUserDetails userDetails,
			RedirectAttributes redirectAttributes,
			Model model) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUuid = UUID.fromString(userUUIDString);
		Optional<User> findUser = userRepository.findById(userUuid);
		
		if (findUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");	
		}
		Optional<List<RegAddress>> addressList = regAddressRepository.findAllByUser(findUser.get());
		model.addAttribute("addressList", addressList.get());	
		return addressList;
	}
	
	// 유저 기본 주소 변경
	@Transactional
	public boolean selectDefaultAddress (
			CustomUserDetails userDetails,
			UUID regAddressId,
			RedirectAttributes redirectAttributes) {
		String userUUIDString = userDetails.getUser().getUserUuidAsString();
		UUID userUuid = UUID.fromString(userUUIDString); 
		Optional<User> findUser = userRepository.findById(userUuid);
		
		if (findUser.isEmpty()) {
			redirectAttributes.addFlashAttribute("result", 1);
			redirectAttributes.addFlashAttribute("resultMsg", "존재하지 않는 ID입니다.");
			return true;
		}
		try {
			Optional<List<RegAddress>> addressList = regAddressRepository.findAllByUser(findUser.get());
			RegAddress defaultAddress = null;
			for (RegAddress address : addressList.get()) {
				if (address.isUserSelectedAddress() == true) {
					defaultAddress = address;
				}
			}
			String defaultAddressUUIDString = defaultAddress.getUserUuidAsString();
			UUID defaultAddressUuid = UUID.fromString(defaultAddressUUIDString);
			
//			String addressUUIDString = regAddress.getUserUuidAsString();
			String addressUUIDString = regAddressId.toString();
			UUID addressUuid = UUID.fromString(addressUUIDString);
			
			if (addressUuid == defaultAddressUuid) {
				redirectAttributes.addFlashAttribute("result", 1);
				redirectAttributes.addFlashAttribute("resultMsg", "이미 기본주소로 등록되어 있습니다.");
				return false;
			} else {
				Optional<RegAddress> findAddress = regAddressRepository.findById(addressUuid);
				defaultAddress.setUserSelectedAddress(false);
				findAddress.get().updateDefaultAddress(true);
				findAddress.get().setUpdatedDate(LocalDateTime.now());
				findUser.get().updateUserSelectedAddress(findAddress.get());
				userService.refreshUserAuthentication(findUser.get().getUserId());
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
		String regAddressIdString = regAddressId.toString();
		UUID regAddressUuid = UUID.fromString(regAddressIdString);
		Optional<RegAddress> findAddress = regAddressRepository.findById(regAddressUuid);
		if (findAddress.isEmpty()) {
			return false;
		}
		try {
			String userUUIDString = userDetails.getUser().getUserUuidAsString();
			UUID userUuid = UUID.fromString(userUUIDString);
			Optional<User> findUser = userRepository.findById(userUuid);
			if (findUser.isEmpty()) {
				return false;
			}
			regAddressRepository.delete(findAddress.get());
			userService.refreshUserAuthentication(findUser.get().getUserId());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	@Transactional
	public boolean updateAddress(CustomUserDetails userDetails,
			UUID regAddressId
			) {
		
		
		
		return false;
		
	}
}
