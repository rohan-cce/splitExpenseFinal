package com.example.splitExpenseFinal.service.impl;

import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.repository.UserRepository;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User createUser(User user) {
         return userRepository.save(user);
    }


    @Override
    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public double showUserBalance(String id) {
        return userRepository.findById(id).get().getBalance();
    }

    @Override
    public void save(User user) {
          userRepository.save(user);
    }
}
