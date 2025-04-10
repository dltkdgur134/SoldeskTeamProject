package com.soldesk6F.ondal.owner.sales.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.Owner;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "sales_management")
public class SalesManagement {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "s_management_id" , nullable = false, unique = true)
	private UUID sManagementId;
	
	@OneToOne
	@JoinColumn(name = "owner_id" , nullable = false)
	private Owner owner;
	
	@OneToOne
	@JoinColumn(name = "store_id" , nullable = false)
	private Store store;
	
	
	
	
	
}
