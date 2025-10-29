package com.salshaikhi.discountservice.entity;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
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
public class Discount {
    @Id
    private String id;
    @Field("code")
    @Indexed(unique = true)
    private String code;
    @Field("condition")
    private DiscountCondition condition;
    @Field("description")
    private String description;
    @Field("amount")
    private double amount;
    @Field("is_percentage")
    private boolean isPercentage;
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

}
