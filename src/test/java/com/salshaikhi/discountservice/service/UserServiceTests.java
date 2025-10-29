package com.salshaikhi.discountservice.service;

import com.salshaikhi.discountservice.entity.User;
import com.salshaikhi.discountservice.repository.UserRepository;
import com.salshaikhi.discountservice.mapper.UserMapper;
import com.salshaikhi.discountservice.dto.UserRequest;
import com.salshaikhi.discountservice.dto.UserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserService userService;

    @Test
    void testGetAll_withNoUsers_returnsEmptyList() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());
        List<UserResponse> result = userService.getAll();
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetAll_withUsers_returnsList() {
        User user1 = new User();
        User user2 = new User();
        UserResponse response1 = new UserResponse();
        UserResponse response2 = new UserResponse();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.userToResponse(user1)).thenReturn(response1);
        when(userMapper.userToResponse(user2)).thenReturn(response2);
        List<UserResponse> result = userService.getAll();
        assertEquals(2, result.size());
        assertTrue(result.contains(response1));
        assertTrue(result.contains(response2));
    }

    @Test
    void testGetOrNotFound_withExistingUser_returnsUser() {
        User user = new User();
        when(userRepository.findById("id")).thenReturn(Optional.of(user));
        User result = userService.getOrNotFound("id");
        assertEquals(user, result);
    }

    @Test
    void testCreateUser_withValidRequest_userCreatedSuccessfully() {
        UserRequest request = mock(UserRequest.class);
        User user = new User();
        UserResponse response = new UserResponse();
        doAnswer(invocation -> null).when(userMapper).requestToUser(any(), any());
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.userToResponse(any(User.class))).thenReturn(response);
        UserResponse result = userService.create(request);
        assertEquals(response, result);
    }

    @Test
    void testDeleteUser_withValidId_userDeletedSuccessfully() {
        doNothing().when(userRepository).deleteById("id");
        userService.delete("id");
        verify(userRepository, times(1)).deleteById("id");
    }

    @Test
    void testGetUserResponse_withExistingUser_returnsUserResponse() {
        User user = new User();
        UserResponse response = new UserResponse();
        when(userRepository.findById("id")).thenReturn(Optional.of(user));
        when(userMapper.userToResponse(user)).thenReturn(response);
        UserResponse result = userService.getUserResponse("id");
        assertEquals(response, result);
    }

    @Test
    void testUpdateUser_withValidRequest_userUpdatedSuccessfully() {
        UserRequest request = mock(UserRequest.class);
        User user = new User();
        UserResponse response = new UserResponse();
        when(userRepository.findById("id")).thenReturn(Optional.of(user));
        doAnswer(invocation -> null).when(userMapper).requestToUser(request, user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.userToResponse(user)).thenReturn(response);
        UserResponse result = userService.update("id", request);
        assertEquals(response, result);
    }
}
