package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.enums.DiscountType;
import com.salshaikhi.discountservice.entity.enums.UserType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Setter
@Getter
public class DiscountRequest {
    @NotNull(message = "Code cannot be null")
    @NotEmpty(message = "Code cannot be empty")
    private String code;
    private String description;
    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    private Double amount;
    @NotNull(message = "discountType cannot be null")
    @Pattern(regexp = "FLAT_RATE|PERCENTAGE",
            message = "discountType must be either 'FLAT_RATE' or 'PERCENTAGE'")
    private DiscountType discountType; // "FLAT_RATE" or "PERCENTAGE"
    private Boolean active;
    @NotNull(message = "Expiry date cannot be null")
    @Future(message = "Expiry date must be in the future")
    private Instant expiryDate;
    // Optional condition fields
    private Integer minAccountAgeYears;
    private Double perAmountSpent;
    @Pattern(regexp = "EMPLOYEE|AFFILIATE|CUSTOMER",
            message = "discountType must be either 'EMPLOYEE', 'AFFILIATE', 'CUSTOMER' or null for any user")
    private UserType userType;
    private Set<String> excludedCategories;
}

