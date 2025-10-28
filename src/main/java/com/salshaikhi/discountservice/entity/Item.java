package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.entity.enums.ItemCategory;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;

@Setter
@Getter
public class Item {
    @Field("name")
    private String name;
    @Field("category")
    private ItemCategory category;
    @Field("price")
    private BigDecimal price;
    @Field("quantity")
    private int quantity;
}
