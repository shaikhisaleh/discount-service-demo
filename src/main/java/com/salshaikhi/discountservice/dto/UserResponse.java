package com.salshaikhi.discountservice.dto;

import com.salshaikhi.discountservice.entity.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String userType;
    private Instant createdAt;
    private Instant updatedAt;
}
