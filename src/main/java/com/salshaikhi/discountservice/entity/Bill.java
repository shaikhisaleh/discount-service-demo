package com.salshaikhi.discountservice.entity;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Document(collection = "bills")
public class Bill {
    @Id
    private String id;
    @Field("user_id")
    private ObjectId userId;
    @Field("items")
    Set<Item> items = new HashSet<>();
    @Field("total_amount")
    private double totalAmount;
    @Field("discounted_amount")
    private double discountedAmount;
    @Field("final_amount")
    private double finalAmount;
    @Field("created_at")
    private Instant createdAt;
}
