package com.soldesk6F.ondal.adminact.entity;


import java.time.LocalDateTime;

import com.soldesk6F.ondal.admin.entity.Admin;

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
@Table(name = "admin_actions")
public class AdminActions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_id", nullable = false, unique = true)
    private Long actionId;  // 활동 ID (PK)

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 관리자 ID (FK)

    @Column(name = "action_type", nullable = false, length = 50)
    private String actionType;  // 관리자 활동 유형 (예: 로그인, 데이터 수정 등)

    @Column(name = "action_description", nullable = true, length = 500)
    private String actionDescription;  // 활동 설명 (옵션)

    @Column(name = "action_timestamp", nullable = false)
    private LocalDateTime actionTimestamp;  // 활동 발생 시간

    @Builder
    public AdminActions(Admin admin, String actionType, String actionDescription, LocalDateTime actionTimestamp) {
        this.admin = admin;
        this.actionType = actionType;
        this.actionDescription = actionDescription;
        this.actionTimestamp = actionTimestamp;
    }
}

