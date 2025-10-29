package com.salshaikhi.discountservice.mapper;

import com.salshaikhi.discountservice.dto.UserRequest;
import com.salshaikhi.discountservice.dto.UserResponse;
import com.salshaikhi.discountservice.entity.User;
import com.salshaikhi.discountservice.entity.enums.UserType;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User requestToUser(UserRequest request, User user) {
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setUserType(UserType.valueOf(request.getUserType()));
        return user;
    }

    public UserResponse userToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setUserType(String.valueOf(user.getUserType()));
        response.setCreatedAt(user.getCreatedAt());
        response.setUpdatedAt(user.getUpdatedAt());
        return response;
    }
}
