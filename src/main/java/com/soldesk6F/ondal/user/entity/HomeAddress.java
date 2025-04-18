package com.soldesk6F.ondal.user.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomeAddress {
	
	private String address;
	private String detailAddress;
	private double latitude;
	private double longitude;
	
}
