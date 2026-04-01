package com.app.quantitymeasurementapp.service;

import com.app.quantitymeasurementapp.user.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    User findByEmail(String email);

    Optional<User> findById(Long id);

    List<User> findAll();

    User updateRole(Long userId, User.Role newRole);

    void deleteUser(Long userId);
}
