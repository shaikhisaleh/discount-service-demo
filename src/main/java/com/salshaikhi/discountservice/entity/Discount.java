package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.dto.BillDto;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Setter
@Getter
@Document(collection = "discounts")
public abstract class Discount {
    @Id
    private String id;
    @Field("code")
    @Indexed(unique = true)
    private String code;
    @Field("description")
    private String description;
    @Field("amount")
    private double amount;
    @Field("active")
    private boolean active = Boolean.TRUE;
    @Field("expiry_date")
    private Instant expiryDate;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;

    public abstract double applyDiscount(BillDto billDto, User user);
}
