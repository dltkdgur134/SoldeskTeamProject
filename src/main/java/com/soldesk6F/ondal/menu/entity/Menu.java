package com.soldesk6F.ondal.menu.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "menu")
public class Menu {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "menu_id" , nullable = false, unique = true)
	private UUID menuId;
	
	@ManyToOne
	@JoinColumn(name = "store_id" , nullable = false)
	private Store store;
	
	@Column(name ="menu_name" , nullable = false, length = 15)
	private String menuName;
	
	@Lob
	@Column(name = "description")
	private String description;
	
	@Column(name = "price",nullable = false)
	private int price;
	
	@Column(name = "menu_img_file_name", length = 100)
	private String menuImgFileName;

	@Column(name = "menu_img_file_extension", length = 10)
	private String menuImgFileExtension;

	@Column(name = "menu_img_file_path", length = 255)
	private String menuImgFilePath;

	@Lob
    @Column(name = "menu_options1")
    private String menuOptions1;
	@Lob
	@Column(name = "menu_options2")
	private String menuOptions2;
	@Lob
	@Column(name = "menu_options3")
	private String menuOptions3;

    @Enumerated(EnumType.STRING)
    @Column(name = "menu_status", length = 20)
    private MenuStatus menuStatus;

    public enum MenuStatus {
        ACTIVE,
        INACTIVE,	
        SOLD_OUT
    }

    @Builder
	public Menu(Store store, String menuName, String description, int price, String menuImgFileName, String menuImgFileExtension,
			String menuImgFilePath, String menuOptions1, String menuOptions2, String menuOptions3, MenuStatus menuStatus) {
		super();
		this.store = store;
		this.menuName = menuName;
		this.description = description;
		this.price = price;
		this.menuImgFileName = menuImgFileName;
		this.menuImgFileExtension = menuImgFileExtension;
		this.menuImgFilePath = menuImgFilePath;
		this.menuOptions1 = menuOptions1;
		this.menuOptions2 = menuOptions2;
		this.menuOptions3 = menuOptions3;
		this.menuStatus = menuStatus.ACTIVE;
	}
	
    
    
    
}
