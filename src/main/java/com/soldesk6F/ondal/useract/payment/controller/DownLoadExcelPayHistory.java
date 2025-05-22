package com.soldesk6F.ondal.useract.payment.controller;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;

import com.soldesk6F.ondal.login.CustomUserDetails;
import com.soldesk6F.ondal.useract.payment.dto.PaymentHistoryDTO;
import com.soldesk6F.ondal.useract.payment.service.PaymentHistoryService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class DownLoadExcelPayHistory {
	
	private final PaymentHistoryService paymentHistoryService ;
	
	@GetMapping("/userPayHistory/download")
	public void downloadUserPayHistoryExcel(@AuthenticationPrincipal CustomUserDetails userDetails,
			@RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "days", required = false) Integer days,
            @RequestParam(name = "usage", required = false) String usage,
	                                        HttpServletResponse response) throws IOException {

	    String userUUID = userDetails.getUser().getUserUuidAsString();

	    // 필터 조건에 따라 데이터 조회
	    List<PaymentHistoryDTO> historyList = paymentHistoryService.getFilteredPaymentHistory(userUUID, status, usage, days);

	    // 엑셀 파일 생성
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("결제내역");

	    // 헤더 생성
	    Row headerRow = sheet.createRow(0);
	    String[] columns = {"결제수단", "금액", "요청일시", "승인일시", "결제상태", "용도", "환불사유"};
	    for (int i = 0; i < columns.length; i++) {
	        Cell cell = headerRow.createCell(i);
	        cell.setCellValue(columns[i]);
	    }

	    // 데이터 입력
	    int rowIdx = 1;
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
	    for (PaymentHistoryDTO dto : historyList) {
	        Row row = sheet.createRow(rowIdx++);
	        row.createCell(0).setCellValue(dto.getPaymentMethod().toString());
	        row.createCell(1).setCellValue(dto.getAmount());
	        row.createCell(2).setCellValue(dto.getRequestedAt().format(formatter));
	        row.createCell(3).setCellValue(dto.getApprovedAt() != null ? dto.getApprovedAt().format(formatter) : "-");
	        row.createCell(4).setCellValue(dto.getPaymentStatus().toString());
	        row.createCell(5).setCellValue(dto.getPaymentUsageType().getDescription());
	        row.createCell(6).setCellValue(dto.getRefundReason() != null ? dto.getRefundReason() : "-");
	    }

	    // 컬럼 너비 자동 조절
	    for (int i = 0; i < columns.length; i++) {
	        sheet.autoSizeColumn(i);
	    }

	    // HTTP 응답 설정
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment; filename=payment_history.xlsx");

	    workbook.write(response.getOutputStream());
	    workbook.close();
	}


}
