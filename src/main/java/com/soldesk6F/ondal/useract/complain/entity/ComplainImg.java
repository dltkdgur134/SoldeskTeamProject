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
import jakarta.persistence.UniqueConstraint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "complain_img",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = {"complain_id" ,"complain_img_file_path"})
		}
)
public class ComplainImg {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "complain_img_id" , nullable = false , unique = true)
	private UUID complainImgId;
	
	@ManyToOne
	@JoinColumn(name = "complain_id",nullable = false)
	private Complain complain;
	
	@Column(name = "complain_img_path",nullable = false,length = 255)
	private String complainImgPath;

	@Builder
	public ComplainImg(Complain complain, String complainImgPath) {
		super();
		this.complain = complain;
		this.complainImgPath = complainImgPath;
	}
	
	
	
}
