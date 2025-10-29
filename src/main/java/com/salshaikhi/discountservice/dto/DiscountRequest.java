package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.DiscountCondition;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class DiscountRequest {
    @NotNull(message = "Code cannot be null")
    @NotEmpty(message = "Code cannot be empty")
    private String code;
    @NotNull(message = "Condition cannot be null")
    private ConditionDto condition;
    private String description;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;
    @NotNull(message = "isPercentage cannot be null")
    private Boolean isPercentage;
    private Boolean active;
    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private Instant expiryDate;
}

