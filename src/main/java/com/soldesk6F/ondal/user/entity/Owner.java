package com.soldesk6F.ondal.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "owner_id", updatable = false, nullable = false)
    private UUID ownerId;

    @OneToOne
    @JoinColumn(name = "user_uuid", nullable = false, unique = true)
    private User user;

    @Column(name = "owner_nickname" , nullable = false , length = 30)
    private String ownerNickname;
    
    @Column(name = "secondary_password",nullable = false , length = 255)
    private String secondaryPassword;  // 비밀번호 해싱 필요
    
    @Column(name = "owner_wallet",nullable = true )
    private int ownerWallet;
    
    @CreationTimestamp
    @Column(name = "registration_date" , updatable = false)
    private LocalDateTime registrationDate;

    
    
    // Owner 생성자에 owner_id와 registrationDate가 없는 이유: 이 둘은 자동으로 생성하는 값이기에 없어도 된다.
    @Builder

	public Owner(User user,String ownerNickname, String secondaryPassword) {
		super();
		this.user = user;
		this.ownerNickname = ownerNickname;
		this.secondaryPassword = secondaryPassword;
		this.ownerNickname = ownerNickname;
	}

    public String getOwnerUuidAsString() {
	    return ownerId != null ? ownerId .toString() : null;
	}
	
    
    
}

