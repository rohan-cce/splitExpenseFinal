package com.example.splitExpenseFinal.controller;


import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.enums.ResponseStatusCode;
import com.example.splitExpenseFinal.responseTemplate.ResponseTemplate;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/create")
    public ResponseTemplate createUser(@RequestBody User user) {
        if (!user.getName().isEmpty()) {
            User user1 = userService.createUser(user);
            if (user1 != null) {
                return new ResponseTemplate(
                        ResponseStatusCode.USER_CREATED.getMessage(),
                        ResponseStatusCode.USER_CREATED.getHttpCode(),
                        user1);
            }
        }
        return new ResponseTemplate(
                ResponseStatusCode.EMPTY_USER_NAME.getMessage(),
                ResponseStatusCode.EMPTY_USER_NAME.getHttpCode(),
                "user not created");
    }


//
//    @GetMapping("/find/{id}")
//    public Optional<User> findUser(@PathVariable("id") String id) {
//        return userService.findById(id);
//    }

    @PostMapping("/show-balance/{id}")
    public ResponseTemplate showUserBalance(@PathVariable("id") String id) {
        if (userService.findById(id).isPresent()) {
            double amount = userService.showUserBalance(id);
            if (amount > 0) {
                return new ResponseTemplate(
                        ResponseStatusCode.USER_NEEDS_TO_GET_AMOUNT.getMessage(),
                        ResponseStatusCode.USER_NEEDS_TO_GET_AMOUNT.getHttpCode(),
                        new String("user need to get : rs " + amount));
            }
            return new ResponseTemplate(
                    ResponseStatusCode.USER_OWES_AMOUNT.getMessage(),
                    ResponseStatusCode.USER_OWES_AMOUNT.getHttpCode(),
                    new String("user owes : rs " + amount));
        }
        return new ResponseTemplate(
                ResponseStatusCode.USER_ID_DOESNT_EXIST.getMessage(),
                ResponseStatusCode.USER_ID_DOESNT_EXIST.getHttpCode(),
                "User id doesnt exist");
    }

}
