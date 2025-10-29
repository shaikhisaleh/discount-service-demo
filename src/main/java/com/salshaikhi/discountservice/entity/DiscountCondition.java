package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class DiscountCondition {
    @Field("min_account_age_years")
    private Integer minAccountAgeYears; //if null, no minimum account age condition
    @Field("user_type")
    private UserType userType; //if null, applies to all user types
    @Field("per_amount_spent")
    private Double perAmountSpent; //if null, no minimum purchase amount condition
    @Field("excluded_categories")
    private Set<String> excludedCategories = new HashSet<>(); //if empty, no excluded categories
}
