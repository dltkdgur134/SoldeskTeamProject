package com.soldesk6F.ondal.admin.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "admin")
public class Admin {

	@Id
    @Column(name = "admin_login_id", nullable = false, unique = true, length = 20)
    private String loginId;

    @Column(name = "admin_password", nullable = false)
    private String password;

    @Builder
    public Admin(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }
}
