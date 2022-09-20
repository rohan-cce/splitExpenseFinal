package com.example.splitExpenseFinal.controller;


import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.returnTemplate.ResponseTemplate;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseTemplate createUser(@RequestBody User user) {
        if (!user.getName().isEmpty()) {
            User user1 = userService.createUser(user);
            user1.setId("#*********************#");
            if (user1 != null) {
                return new ResponseTemplate(
                        HttpStatus.CREATED.getReasonPhrase(),
                        HttpStatus.CREATED.value(),
                        user1);
            }
        }
        return new ResponseTemplate(
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                "user not created");
    }

    @GetMapping("/find/{id}")
    public Optional<User> findUser(@PathVariable("id") String id) {
        return userService.findById(id);
    }

    @PostMapping("/show-balance/{id}")
    public ResponseTemplate showUserBalance(@PathVariable("id") String id) {
        if (userService.findById(id).isPresent()) {
            double amount = userService.showUserBalance(id);
            if (amount > 0) {
                return new ResponseTemplate(
                        HttpStatus.OK.getReasonPhrase(),
                        HttpStatus.OK.value(),
                        new String("user need to get : rs " + amount));
            }
            return new ResponseTemplate(
                    HttpStatus.OK.getReasonPhrase(),
                    HttpStatus.OK.value(),
                    new String("user owes : rs " + amount));
        }
        return new ResponseTemplate(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                "User id doesnt exist");
    }

}
