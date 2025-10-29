package com.salshaikhi.discountservice.controller;

import com.salshaikhi.discountservice.dto.UserRequest;
import com.salshaikhi.discountservice.dto.UserResponse;
import com.salshaikhi.discountservice.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("v1/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserResponse getById(@PathVariable String id) {
        return userService.getUserResponse(id);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserResponse create(@Valid @RequestBody UserRequest user) {
        return userService.create(user);
    }

    @PutMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public UserResponse update(@PathVariable String id,@Valid @RequestBody UserRequest user) {
        return userService.update(id, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }
}
