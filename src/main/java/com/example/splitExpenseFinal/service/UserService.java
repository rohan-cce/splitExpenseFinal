package com.example.splitExpenseFinal.service;

import com.example.splitExpenseFinal.document.User;

import java.util.Optional;

public interface UserService {

    User createUser(User user);

    Optional<User> findById(String id);

    double showUserBalance(String id);

    void save(User user);
}
