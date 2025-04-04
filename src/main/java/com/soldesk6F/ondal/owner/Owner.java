package com.soldesk6F.ondal.owner;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Owner {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "owner_id", updatable = false, nullable = false, unique = true)
    private UUID ownerId;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User userId;

    @Column(name = "secondary_password",nullable = false)
    private String secondaryPassword;  // 비밀번호 해싱 필요

    @CreationTimestamp
    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    
    
    // Owner 생성자에 owner_id와 registrationDate가 없는 이유: 이 둘은 자동으로 생성하는 값이기에 없어도 된다.
	public Owner(User userId, String secondaryPassword) {
		super();
		this.userId = userId;
		this.secondaryPassword = secondaryPassword;
	}

    
	
    
    
}

