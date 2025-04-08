package com.soldesk6F.ondal.adminact.entity;


import java.time.LocalDateTime;

import com.soldesk6F.ondal.admin.entity.Admin;
import com.soldesk6F.ondal.store.entity.Store;
import com.soldesk6F.ondal.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "user_warning")
public class UserWarning {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warning_id", nullable = false, unique = true)
    private Long warningId;  // 경고 ID (PK)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;  // 유저 ID (FK)

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 관리자 ID (FK)

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;  // 상점 ID (FK)

    @Column(name = "warning_type", nullable = false, length = 50)
    private String warningType;  // 경고 유형

    @Column(name = "warning_message", nullable = false, length = 500)
    private String warningMessage;  // 경고 메시지

    @Column(name = "warning_date", nullable = false)
    private LocalDateTime warningDate;  // 경고 날짜

    @Builder
    public UserWarning(User user, Admin admin, Store store, String warningType, String warningMessage, LocalDateTime warningDate) {
        this.user = user;
        this.admin = admin;
        this.store = store;
        this.warningType = warningType;
        this.warningMessage = warningMessage;
        this.warningDate = warningDate;
    }
}

