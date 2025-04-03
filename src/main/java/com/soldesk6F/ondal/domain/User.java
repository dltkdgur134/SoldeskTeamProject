package com.soldesk6F.ondal.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @Column(name = "user_id", length = 15)
    private String userId;

    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;

    @Column(name = "user_name", nullable = false, length = 10)
    private String userName;

    @Column(name = "user_profile_extension", nullable = false, length = 10)
    private String userProfileExtension;

    @Column(name = "user_phone", nullable = false, length = 13)
    private String userPhone;

    @Column(nullable = false, length = 20)
    private String nickname;

    @Column(nullable = false, length = 100, unique = true)
    private String email;

    @Column(name = "social_login_provider", nullable = false, length = 30)
    private String socialLoginProvider;

    @Column(name = "user_address", nullable = false, length = 80)
    private String userAddress;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "user_profile_name", nullable = false, length = 255)
    private String userProfileName;

    @Column(name = "user_profile_path", nullable = false, length = 255)
    private String userProfilePath;

    @Column(nullable = false, length = 50)
    private String username;
}