package com.legaltrack.dto.casefile;

import com.legaltrack.enums.OrderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {

    @NotNull(message = "Order date is required")
    private LocalDate orderDate;

    @NotBlank(message = "Order title is required")
    private String title;

    @Builder.Default
    private OrderType orderType = OrderType.INTERIM_ORDER;

    private Long documentId;
    private String summary;
}
