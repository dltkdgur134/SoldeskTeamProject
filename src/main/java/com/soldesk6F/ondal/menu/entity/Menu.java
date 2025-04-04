package com.soldesk6F.ondal.menu.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
}
