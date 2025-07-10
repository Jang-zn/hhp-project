package io.hhplus.tdd.user.service;

import java.util.Optional;

import io.hhplus.tdd.user.model.User;

public interface UserService {
    User signup(String name);
    Optional<User> findUserById(long id);

    /**
     * 이름 수정
     */
    User updateName(long id, String newName);

    /**
     * 사용자 탈퇴(상태 변경)
     */
    User retireUser(long id);
} 