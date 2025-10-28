package com.salshaikhi.discountservice.entity;

import com.mongodb.lang.NonNull;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.time.LocalDateTime;

@Setter
@Getter
@Document(collection = "discounts")
public class Discount {
    @Id
    private String id;
    @Field("code")
    @Indexed(unique = true)
    private String code;
    @Field("description")
    private String description;
    @Field("amount")
    private double amount;
    @Field("is_percentage")
    private boolean isPercentage;
    @Field("active")
    private boolean active;
    @Field("expiry_date")
    private Instant expiryDate;


}
