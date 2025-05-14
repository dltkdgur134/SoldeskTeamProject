package com.soldesk6F.ondal.useract.payment.repository;

import java.util.UUID;

import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.soldesk6F.ondal.useract.payment.entity.Payment;

import jakarta.transaction.Transactional;


public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}
