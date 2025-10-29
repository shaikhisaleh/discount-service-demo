package com.salshaikhi.discountservice.service;

import com.salshaikhi.discountservice.dto.UserRequest;
import com.salshaikhi.discountservice.dto.UserResponse;
import com.salshaikhi.discountservice.entity.User;
import com.salshaikhi.discountservice.exception.NotFoundException;
import com.salshaikhi.discountservice.mapper.UserMapper;
import com.salshaikhi.discountservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<UserResponse> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::userToResponse).toList();
    }

    public User getOrNotFound(String id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    public UserResponse getUserResponse(String id) {
        User user = getOrNotFound(id);
        return userMapper.userToResponse(user);
    }

    public UserResponse create(UserRequest request) {
        User newUser = new User();
        userMapper.requestToUser(request, newUser);
        return userMapper.userToResponse(userRepository.save(newUser));
    }

    public UserResponse update(String id, UserRequest request) {
        User user = getOrNotFound(id);
        userMapper.requestToUser(request, user);
        return userMapper.userToResponse(userRepository.save(user));
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }

    public User getByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email).orElseThrow(
                () -> new NotFoundException("User with email: " + email + " not found"));
    }
}

