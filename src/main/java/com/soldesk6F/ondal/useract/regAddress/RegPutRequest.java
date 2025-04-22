package com.soldesk6F.ondal.useract.regAddress;

import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegPutRequest {
	
	private UUID regAddressId;
	private String address;
	
	
	
}
