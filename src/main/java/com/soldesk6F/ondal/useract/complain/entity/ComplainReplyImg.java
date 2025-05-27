package com.soldesk6F.ondal.useract.complain.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "complain_reply_img")
public class ComplainReplyImg {

	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "complain_reply_img_id" , nullable = false , unique = true)
	private UUID complainReplyImgId;
	
	@ManyToOne
	@JoinColumn(name = "complain_reply_id",nullable = false)
	private ComplainReply complainReply;
	
	@Column(name = "complain_reply_img",nullable = false,length = 255)
	private String complainReplyImg;

	@Builder
	public ComplainReplyImg(ComplainReply complainReply, String complainReplyImg) {
		super();
		this.complainReply = complainReply;
		this.complainReplyImg = complainReplyImg;
	}
	
	public String getComplainImgIdAsString() {
	    return complainReplyImgId != null ? complainReplyImgId .toString() : null;
	}
	
}
