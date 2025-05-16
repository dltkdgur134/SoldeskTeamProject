package com.soldesk6F.ondal.functions;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

@Service
public class DateFunctions {
	
	// 두 날짜 사이에 기간이 얼마나 지났는지 계산하여 long 반환 
	public Long getDaysPassedAsLong(LocalDateTime createdDateTime) {		
		LocalDate createdDate = createdDateTime.toLocalDate();
		LocalDate now = LocalDate.now();
		long daysBetween = ChronoUnit.DAYS.between(createdDate, now);
		return daysBetween;
	}
	
	
	// 두 날짜 사이에 기간이 얼마나 지났는지 계산하여 String 반환
	public String getDaysPassedAsString(LocalDateTime createdDate) {
		LocalDateTime now = LocalDateTime.now();
		
		long seconds = ChronoUnit.SECONDS.between(createdDate, now);
		if (seconds < 60) {
			return seconds + "초 전";
		}
		
		long minutes = seconds / 60;
		if (minutes < 60) {
			return minutes + "분 전";
		}
		
		long hours = minutes / 60;
		if (hours < 24) {
			return hours + "시간 전";
		}
		
		long days = hours / 24;
		long weeks = ChronoUnit.WEEKS.between(createdDate, now);
		long months = ChronoUnit.MONTHS.between(createdDate, now);
		long years = ChronoUnit.YEARS.between(createdDate, now);
		
		if (days < 7) {
			return days + "일 전";
		}
		
		if (months < 1) {
			return weeks + "주 전";
		}
		
		if (months < 12) {
			return months + "개월 전";
		}
		
		return years + "년 전";
	}
	
	public String changeDateToString(LocalDateTime date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.DD (E) hh:mm");
		return sdf.format(date);
	}
	
}

