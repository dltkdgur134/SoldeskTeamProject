package com.soldesk6F.ondal.owner.wallet.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import com.soldesk6F.ondal.user.entity.Owner;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "owner_wallet_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OwnerWalletHistory {

    @Id
    @Column(name = "owner_wallet_history_id",updatable = false, nullable = false)
    private UUID ownerWalletHistoryId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Column(name = "fee", nullable = false)
    private int fee;

    @Column(name = "final_amount", nullable = false)
    private int finalAmount;

    @Column(name = "description")
    private String description;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}
