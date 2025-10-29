package com.salshaikhi.discountservice.mapper;

import com.salshaikhi.discountservice.dto.ConditionDto;
import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.DiscountCondition;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Condition;

@Component
public class DiscountMapper {
    public Discount requestToDiscount(DiscountRequest request, Discount discount) {
        // Trim and convert code to uppercase for consistency
        discount.setCode(request.getCode().trim().toUpperCase());
        discount.setDescription(request.getDescription());
        discount.setCondition(toEntity(request.getCondition()));
        discount.setAmount(request.getAmount());
        discount.setPercentage(request.getIsPercentage());
        discount.setActive(request.getActive());
        discount.setExpiryDate(request.getExpiryDate());
        return discount;
    }

    public DiscountResponse discountToResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setCode(discount.getCode());
        response.setDescription(discount.getDescription());
        response.setCondition(toDto(discount.getCondition()));
        response.setAmount(discount.getAmount());
        response.setPercentage(discount.isPercentage());
        response.setActive(discount.isActive());
        response.setExpiryDate(discount.getExpiryDate());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());
        return response;
    }

    private DiscountCondition toEntity(ConditionDto dto) {
        if (dto == null) {
            return null;
        }
        DiscountCondition condition = new DiscountCondition();
        condition.setExcludedCategories(dto.getExcludedCategories());
        condition.setUserType(dto.getUserType());
        condition.setExcludedCategories(dto.getExcludedCategories());
        condition.setPerAmountSpent(dto.getPerAmountSpent());
        return condition;
    }

    private ConditionDto toDto(DiscountCondition condition) {
        if (condition == null) {
            return new ConditionDto();
        }
        ConditionDto dto = new ConditionDto();
        dto.setExcludedCategories(condition.getExcludedCategories());
        dto.setUserType(condition.getUserType());
        dto.setExcludedCategories(condition.getExcludedCategories());
        dto.setPerAmountSpent(condition.getPerAmountSpent());

        return dto;
    }
}

