package com.soldesk6F.ondal.rider;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Rider {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column( updatable = false, nullable = false, unique = true)
	private UUID riderId;
	
	@OneToOne
	@JoinColumn(name = "user_id", nullable = false, unique =true)
	private User userId;
	
	@Column(name = "secondary_password" , nullable = false)
	private String secondart_Password;
	
	
	
	
}
