package com.salshaikhi.discountservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BillDto {
    @NotNull(message = "Items list cannot be null")
    List<ItemDto> items = new ArrayList<>();
    List<String> appliedDiscounts = new ArrayList<>();
    Double totalPrice;
    Double priceAfterDiscount;
}