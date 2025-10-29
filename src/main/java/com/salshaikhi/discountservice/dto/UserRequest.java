package com.salshaikhi.discountservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserRequest {
    @NotNull(message = "Username cannot be null")
    @NotBlank(message = "Username cannot be blank")
    private String username;
    @NotNull(message = "Email cannot be null")
    @Email(message = "Invalid email format")
    private String email;
    @NotNull(message = "User type cannot be null")
    @Pattern(regexp = "^(CUSTOMER|EMPLOYEE|AFFILIATE)$",
            message = "User type must be CUSTOMER, EMPLOYEE, or AFFILIATE")
    private String userType;
}
