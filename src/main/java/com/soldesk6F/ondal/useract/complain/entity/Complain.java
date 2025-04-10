package com.soldesk6F.ondal.useract.complain.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "complain")
public class Complain {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "complain_id", nullable = false, unique = true)
    private UUID complainId;

    @ManyToOne
    @JoinColumn(name = "user_uuid", nullable = true)
    private User user;

    @Column(name = "guest_id", length = 36)
    private String guestId;

    @Column(name = "complain_password", length = 4)
    private String complainPassword;

    @Column(name = "complain_title", nullable = false, length = 50)
    private String complainTitle;

    @Lob
    @Column(name = "complain_content", nullable = false)
    private String complainContent;
    
    
    @Enumerated(EnumType.STRING)
    @Column(name = "complain_status", nullable = false)
    private ComplainStatus complainStatus = ComplainStatus.PENDING; // 기본값
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    @OneToMany(mappedBy = "complain", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplainReply> replies = new ArrayList<>();

    @Builder
    public Complain(User user, String guestId, String complainPassword, String complainTitle, String complainContent,
			 ComplainStatus complainStatus) {
		super();
		this.user = user;
		this.guestId = guestId;
		this.complainPassword = complainPassword;
		this.complainTitle = complainTitle;
		this.complainContent = complainContent;
		this.complainStatus = complainStatus != null ? complainStatus : ComplainStatus.PENDING;
	}


	public enum ComplainStatus {
    	PENDING,      // 처리 전
    	IN_PROGRESS,  // 처리 중
    	RESOLVED      // 처리 완료
    }
	
	public String getComplainUuidAsString() {
	    return complainId != null ? complainId .toString() : null;
	}
	
}
