package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class ConditionDto {
    private Integer minAccountAgeYears; //if null, no minimum account age condition
    private UserType userType; //if null, applies to all user types
    private Double perAmountSpent; //if null, no minimum purchase amount condition
    private Set<String> excludedCategories = new HashSet<>();
}
