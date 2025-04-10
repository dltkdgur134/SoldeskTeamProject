package com.soldesk6F.ondal.useract.complain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.admin.entity.Admin;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "complain_reply")
public class ComplainReply {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "complain_reply_id", nullable = false, unique = true)
    private UUID complainReplyId;

    @ManyToOne
    @JoinColumn(name = "complain_id", nullable = false)
    private Complain complain;

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Lob
    @Column(name = "reply_content", nullable = false)
    private String replyContent;

    @CreationTimestamp
    @Column(name = "replied_date", nullable = false, updatable = false)
    private LocalDateTime repliedDate;

    @Builder
    public ComplainReply(Complain complain,Admin admin, String replyContent) {
        this.complain = complain;
        this.admin = admin;
        this.replyContent = replyContent;
    }
    public String getComplainReplyIdAsString() {
	    return complainReplyId != null ? complainReplyId .toString() : null;
	}
    
    
}
