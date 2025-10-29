package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.enums.DiscountType;
import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Set;

@Setter
@Getter
public class DiscountResponse {
    private String id;
    private String code;
    private String description;
    private double amount;
    private DiscountType discountType;
    private boolean active;
    private Instant expiryDate;
    private Instant createdAt;
    private Instant updatedAt;

    // Condition fields (formerly in ConditionDto)
    private Integer minAccountAgeYears;
    private Double perAmountSpent;
    private UserType userType;
    private Set<String> excludedCategories;
}

