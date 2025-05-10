package com.soldesk6F.ondal.rider.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.soldesk6F.ondal.rider.dto.RiderManagementDto;
import com.soldesk6F.ondal.rider.entity.DeliverySales;
import com.soldesk6F.ondal.rider.entity.RiderManagement;
import com.soldesk6F.ondal.rider.repository.DeliverySalesRepository;
import com.soldesk6F.ondal.rider.repository.RiderManagementRepository;
import com.soldesk6F.ondal.user.entity.Rider;
import com.soldesk6F.ondal.user.repository.RiderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiderManagementService {

    private final RiderRepository riderRepository;
    private final DeliverySalesRepository deliverySalesRepository;
    private final RiderManagementRepository riderManagementRepository;

    // 생성자 주입은 @RequiredArgsConstructor가 자동으로 해줍니다.
    // 따로 생성자를 명시할 필요가 없습니다.

    public void createInitialRiderManagement(Rider rider) {
        RiderManagement mgmt = new RiderManagement();
        mgmt.setRider(rider);
        mgmt.setTotalSales(0);
        mgmt.setTotalVat(0);
        riderManagementRepository.save(mgmt);
    }
    
    
    public RiderManagementDto getRiderManagementInfo(String userId) {
        // Rider 정보 가져오기
        Rider rider = riderRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new RuntimeException("Rider not found"));

        // RiderManagement 정보 가져오기
        RiderManagement riderManagement = riderManagementRepository.findByRider(rider)
                .orElseThrow(() -> new RuntimeException("Rider Management not found"));

        // 해당 Rider의 DeliverySales 정보 가져오기
        List<DeliverySales> deliverySales = deliverySalesRepository.findByRiderManagement_Rider(rider);

        // RiderManagementDto 생성
        return RiderManagementDto.of(riderManagement, deliverySales);
    }
}
