package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.dto.BillDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Setter
@Getter
@TypeAlias("FlatRateDiscount")
@Document(collection = "discounts")
public class FlatRateDiscount extends Discount {
    @Field("min_account_age_years")
    private Integer minAccountAgeYears; // if null, no minimum account age condition
    @Field("per_amount_spent")
    private Double perAmountSpent; // if null, no minimum purchase amount condition

    @Override
    public double applyDiscount(BillDto billDto, User user) {
        // Check if user meets account age requirement
        if (minAccountAgeYears != null) {
            LocalDate created = user.getCreatedAt().atZone(ZoneId.systemDefault()).toLocalDate();
            Long accountAgeYears = ChronoUnit.YEARS.between(created, LocalDate.now());
            if (accountAgeYears < minAccountAgeYears) {
                return 0.0;
            }
        }

        // Calculate total bill amount
        Double billPrice = billDto.getItems().stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();

        // Check if bill meets minimum amount requirement
        if (perAmountSpent != null && billPrice < perAmountSpent) {
            return 0.0;
        }

        // Calculate flat rate discount based on multiples of perAmountSpent
        if (perAmountSpent != null) {
            Integer flatDiscountMultiples = (int) Math.floor(billPrice / perAmountSpent);
            return flatDiscountMultiples * getAmount();
        }

        // If no perAmountSpent requirement, apply flat discount once
        return Math.min(getAmount(), billPrice);
    }
}
