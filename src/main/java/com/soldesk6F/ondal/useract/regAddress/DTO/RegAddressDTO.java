package com.soldesk6F.ondal.useract.regAddress.DTO;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegAddressDTO {
	
	private UUID regAddressId;
	private UUID userUuid;
	private String address;
	private String detailAddress;
	private String userAddressLatitude;
	private String userAddressLongitude;
	
	public String getRegAddressUuidAsString() {
	    return regAddressId != null ? regAddressId .toString() : null;
	}
	
}
