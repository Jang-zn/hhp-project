package io.hhplus.tdd.user.controller;

import io.hhplus.tdd.user.model.User;
import io.hhplus.tdd.user.service.UserService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (user.name() == null || user.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new io.hhplus.tdd.common.ErrorResponse(
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_NAME.getCode(),
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_NAME.getMessage())
            );
        }
        User savedUser = userService.signup(user.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findUserById(@PathVariable long id) {
        Optional<User> user = userService.findUserById(id);
        return ResponseEntity.ok(user.orElse(null));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateName(@PathVariable long id, @RequestBody User user) {
        if (user.name() == null || user.name().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(
                new io.hhplus.tdd.common.ErrorResponse(
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_NAME.getCode(),
                    io.hhplus.tdd.common.constants.ErrorCode.INVALID_NAME.getMessage())
            );
        }
        User updatedUser = userService.updateName(id, user.name());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<User> retireUser(@PathVariable long id) {
        User retiredUser = userService.retireUser(id);
        return ResponseEntity.ok(retiredUser);
    }

} 