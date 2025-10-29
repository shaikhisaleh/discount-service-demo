package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.dto.BillDto;
import com.salshaikhi.discountservice.dto.ItemDto;
import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@TypeAlias("PercentageBasedDiscount")
@Document(collection = "discounts")
public class PercentageBasedDiscount extends Discount {
    @Field("user_type")
    private UserType userType; // if null, applies to all user types
    @Field("excluded_categories")
    private Set<String> excludedCategories = new HashSet<>(); // if empty, no excluded categories

    @Override
    public double applyDiscount(BillDto billDto, User user) {
        if (userType != null && !user.getUserType().equals(userType)) {
            return 0.0;
        }
        double discountAmount = 0.0;
        // Apply percentage discount only to items not in excluded categories
        for (ItemDto item : billDto.getItems()) {
            if (!excludedCategories.contains(item.getCategory())) {
                Double itemTotal = item.getPrice() * item.getQuantity();
                discountAmount += itemTotal * (getAmount() / 100.0);
            }
        }

        return discountAmount;
    }
}