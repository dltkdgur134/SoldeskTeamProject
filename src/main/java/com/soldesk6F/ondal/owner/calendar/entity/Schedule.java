package com.soldesk6F.ondal.owner.calendar.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import com.soldesk6F.ondal.store.entity.Store;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "schedule")
public class Schedule {
	@Id
	@GeneratedValue
	@UuidGenerator
	@Column(name = "schedule_id", updatable = false, nullable = false, unique = true)
	private UUID scheduleId;

	@ManyToOne
	@JoinColumn(name = "calendar_id", nullable = false)
	private OwnerCalendar ownerCalendar;

	@ManyToOne
	@JoinColumn(name = "store_id", nullable = false)
	private Store store;

	@Column(name = "schedule_title", nullable = false, length = 30)
	private String scheduleTitle;
	
	@Lob
	@Column(name = "schedule_content")
	private String scheduleContent;

	@CreationTimestamp
	@Column(name = "created_date", updatable = false)
	private LocalDateTime createdDate;

	@UpdateTimestamp
	@Column(name = "updated_date")
	private LocalDateTime updatedDate;

	@Column(name = "schedule_img_name", length = 255)
	private String scheduleImgName;

	@Column(name = "schedule_img_extension", length = 10)
	private String scheduleImgExtension;

	@Column(name = "schedule_img_path", length = 255)
	private String scheduleImgPath;

	@Column(name = "schedule_address", length = 80)
	private String scheduleAddress;

	@Column(name = "schedule_start_time", nullable = false)
	private LocalDateTime scheduleStartTime;

	@Column(name = "schedule_end_time", nullable = false)
	private LocalDateTime scheduleEndTime;

	@Column(name = "alarm_timing", nullable = false)
	private int alarmTiming; // 일정 시작 몇 분 전에 알람을 보낼지 (예: 10분 전)
	
	@PrePersist
    @PreUpdate
    public void validateTimes() {
        if (this.scheduleStartTime != null && this.scheduleEndTime != null) {
            if (this.scheduleStartTime.isAfter(this.scheduleEndTime)) {
                throw new IllegalArgumentException("Schedule start time must be before end time.");
            }
        }
    }
	@Builder
	public Schedule(OwnerCalendar calendar,Store store, String scheduleTitle, String scheduleContent, String scheduleImgName,
			String scheduleImgExtension, String scheduleImgPath, String scheduleAddress,
			LocalDateTime scheduleStartTime, LocalDateTime scheduleEndTime, int alarmTiming) {
		this.ownerCalendar = calendar;
		this.store = store;
		this.scheduleTitle = scheduleTitle;
		this.scheduleContent = scheduleContent;
		this.scheduleImgName = scheduleImgName;
		this.scheduleImgExtension = scheduleImgExtension;
		this.scheduleImgPath = scheduleImgPath;
		this.scheduleAddress = scheduleAddress;
		this.scheduleStartTime = scheduleStartTime;
		this.scheduleEndTime = scheduleEndTime;
		this.alarmTiming = alarmTiming;
	}

}
