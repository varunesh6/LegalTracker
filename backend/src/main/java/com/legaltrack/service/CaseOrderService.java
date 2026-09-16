package com.legaltrack.service;

import com.legaltrack.dto.casefile.CaseOrderDto;
import com.legaltrack.dto.casefile.CreateOrderRequest;

import java.util.List;

public interface CaseOrderService {
    CaseOrderDto addOrder(Long caseId, CreateOrderRequest request);
    List<CaseOrderDto> getOrdersForCase(Long caseId);
}
