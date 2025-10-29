package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.DiscountCondition;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class DiscountResponse {
    private String id;
    private String code;
    private ConditionDto condition;
    private String description;
    private double amount;
    private boolean isPercentage;
    private boolean active;
    private Instant expiryDate;
    private Instant createdAt;
    private Instant updatedAt;
}

