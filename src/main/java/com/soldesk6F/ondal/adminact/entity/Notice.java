package com.soldesk6F.ondal.adminact.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.soldesk6F.ondal.admin.entity.Admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
@Table(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", nullable = false, unique = true)
    private Long noticeId;  // 공지 ID (PK)

    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;  // 공지 작성자 관리자 ID (FK)

    @Column(name = "title", nullable = false, length = 100)
    private String title;  // 공지 제목

    @Lob
    @Column(name = "content", nullable = false)
    private String content;  // 공지 내용

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;  // 공지 작성 일시

    @Builder
    public Notice(Admin admin, String title, String content) {
        this.admin = admin;
        this.title = title;
        this.content = content;
    }
}

