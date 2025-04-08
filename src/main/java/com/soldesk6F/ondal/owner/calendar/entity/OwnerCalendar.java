package com.soldesk6F.ondal.owner.calendar.entity;

import java.util.UUID;

import org.hibernate.annotations.UuidGenerator;

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
@Table(name="onwer_calendar")
public class OwnerCalendar {
	@Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "calendar_id", updatable = false, nullable = false, unique = true)
    private UUID calendarId;

    @OneToOne
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private Owner owner;  // Owner 테이블과 1:1 매핑

    @Column(name = "calendar_name", length = 20, nullable = false)
    private String calendarName;

    public OwnerCalendar(Owner owner, String calendarName) {
        this.owner = owner;
        this.calendarName = calendarName;
    }
}
