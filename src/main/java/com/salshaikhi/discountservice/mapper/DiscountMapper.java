package com.salshaikhi.discountservice.mapper;

import com.salshaikhi.discountservice.dto.DiscountRequest;
import com.salshaikhi.discountservice.dto.DiscountResponse;
import com.salshaikhi.discountservice.entity.Discount;
import com.salshaikhi.discountservice.entity.FlatRateDiscount;
import com.salshaikhi.discountservice.entity.PercentageBasedDiscount;
import com.salshaikhi.discountservice.entity.enums.DiscountType;
import org.springframework.stereotype.Component;

@Component
public class DiscountMapper {
    public Discount requestToDiscount(DiscountRequest request, Discount discount) {
        // Trim and convert code to uppercase for consistency
        discount.setCode(request.getCode().trim().toUpperCase());
        discount.setDescription(request.getDescription());
        discount.setAmount(request.getAmount());
        discount.setActive(request.getActive());
        discount.setExpiryDate(request.getExpiryDate());

        // Set type-specific fields
        if (discount instanceof FlatRateDiscount) {
            FlatRateDiscount flatRate = (FlatRateDiscount) discount;
            flatRate.setMinAccountAgeYears(request.getMinAccountAgeYears());
            flatRate.setPerAmountSpent(request.getPerAmountSpent());
        } else if (discount instanceof PercentageBasedDiscount) {
            PercentageBasedDiscount percentage = (PercentageBasedDiscount) discount;
            percentage.setUserType(request.getUserType());
            percentage.setExcludedCategories(request.getExcludedCategories());
        }

        return discount;
    }

    public DiscountResponse discountToResponse(Discount discount) {
        DiscountResponse response = new DiscountResponse();
        response.setId(discount.getId());
        response.setCode(discount.getCode());
        response.setDescription(discount.getDescription());
        response.setAmount(discount.getAmount());
        response.setActive(discount.isActive());
        response.setExpiryDate(discount.getExpiryDate());
        response.setCreatedAt(discount.getCreatedAt());
        response.setUpdatedAt(discount.getUpdatedAt());

        // Set condition fields based on discount type
        if (discount instanceof FlatRateDiscount) {
            FlatRateDiscount flatRate = (FlatRateDiscount) discount;
            response.setMinAccountAgeYears(flatRate.getMinAccountAgeYears());
            response.setPerAmountSpent(flatRate.getPerAmountSpent());
            response.setDiscountType(DiscountType.FLAT_RATE);
        } else if (discount instanceof PercentageBasedDiscount) {
            PercentageBasedDiscount percentage = (PercentageBasedDiscount) discount;
            response.setUserType(percentage.getUserType());
            response.setExcludedCategories(percentage.getExcludedCategories());
            response.setDiscountType(DiscountType.PERCENTAGE_BASED);
        }

        return response;
    }
}

