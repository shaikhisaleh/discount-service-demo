package com.salshaikhi.discountservice.entity;

import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;

@Getter
@Setter
@Document(collection = "users")
public class User {
    @Id
    private String id;
    @Field("username")
    @Indexed(unique = true)
    private String username;
    @Field("email")
    @Indexed(unique = true)
    private String email;
    @Field("user_type")
    private UserType userType;
    @CreatedDate
    @Field("created_at")
    private Instant createdAt;
    @LastModifiedDate
    @Field("updated_at")
    private Instant updatedAt;
}
