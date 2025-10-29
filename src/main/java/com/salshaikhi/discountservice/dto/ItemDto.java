package com.salshaikhi.discountservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemDto {
    @NotNull(message = "Item name cannot be null")
    @NotBlank(message = "Item name cannot be blank")
    private String name;
    @NotNull(message = "Item price cannot be null")
    @PositiveOrZero(message = "Item price must be zero or positive")
    private Double price;
    @NotNull(message = "Item quantity cannot be null")
    @PositiveOrZero(message = "Item quantity must be zero or positive")
    private Integer quantity;
    @NotNull(message = "Item category cannot be null")
    @NotBlank(message = "Item category cannot be blank")
    private String category;
}
