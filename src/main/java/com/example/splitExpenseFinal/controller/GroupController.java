package com.example.splitExpenseFinal.controller;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.dto.EqualSplitDto;
import com.example.splitExpenseFinal.enums.ResponseStatusCode;
import com.example.splitExpenseFinal.responseTemplate.ResponseTemplate;
import com.example.splitExpenseFinal.service.ExpenseService;
import com.example.splitExpenseFinal.service.GroupService;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/group")
public class GroupController {

    private Expense expense;
    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    ExpenseService expenseService;


    @PostMapping("/create")
    public ResponseTemplate createGroup(@RequestBody Group group) {
        if (!group.getName().isEmpty()) {
            Group group1 = groupService.createGroup(group);

            if (group1 != null) {
                return new ResponseTemplate(
                        ResponseStatusCode.GROUP_CREATED.getMessage(),
                        ResponseStatusCode.GROUP_CREATED.getHttpCode(),
                        group1);
            }
        }
        return new ResponseTemplate(
                ResponseStatusCode.EMPTY_GROUP_NAME.getMessage(),
                ResponseStatusCode.EMPTY_GROUP_NAME.getHttpCode(),
                "group not created");
    }


    @PostMapping("/add/user/{groupId}/{userId}")
    public ResponseTemplate addUserToGroup(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId) {
        if (groupService.findByGroupId(groupId).isPresent() && userService.findById(userId).isPresent()) {
            Group group = groupService.findByGroupId(groupId).get();
            group.setId(group.getId());
            Map<String, Double> userMap = group.getCurrentBalance();
            if (!userMap.containsKey(userId)) {
                group.setId(group.getId());
                userMap.put(userId, 0.0);
                groupService.save(group);
                return new ResponseTemplate(
                        ResponseStatusCode.USER_ADDED_IN_GROUP.getMessage(),
                        ResponseStatusCode.USER_ADDED_IN_GROUP.getHttpCode(),
                        "User Added in group");
            }
            return new ResponseTemplate(
                    ResponseStatusCode.USER_ALREADY_PRESENT_IN_GROUP.getMessage(),
                    ResponseStatusCode.USER_ALREADY_PRESENT_IN_GROUP.getHttpCode(),
                    "User Already present");

        }
        return new ResponseTemplate(
                ResponseStatusCode.INVALID_USER_ID_OR_GROUP_ID.getMessage(),
                ResponseStatusCode.INVALID_USER_ID_OR_GROUP_ID.getHttpCode(),
                "Invalid userId or groupId");
    }

    @PostMapping("/create/equal-expense")
    public ResponseTemplate createEqualExpense(@RequestBody EqualSplitDto equalSplitDto) {
        if (expenseService.checkEqualSplitDto(equalSplitDto)) {
            String validationResult = expenseService.equalExpenseValidation(equalSplitDto);
            if (validationResult.equalsIgnoreCase("Success")) {
                expenseService.createEqualExpense(equalSplitDto, null);
                return new ResponseTemplate(
                        ResponseStatusCode.EQUAL_EXPENSE_CREATED.getMessage(),
                        ResponseStatusCode.EQUAL_EXPENSE_CREATED.getHttpCode(),
                        validationResult);
            }
            return new ResponseTemplate(
                    ResponseStatusCode.EQUAL_EXPENSE_VALIDATION_FAILED.getMessage(),
                    ResponseStatusCode.EQUAL_EXPENSE_VALIDATION_FAILED.getHttpCode(),
                    validationResult);
        }
        return new ResponseTemplate(
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getMessage(),
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getHttpCode(),
                "request body incorrect");
    }

    @PutMapping("/edit/equal-expense/{id}")
    public ResponseTemplate editEqualExpense(@PathVariable("id") String id, @RequestBody EqualSplitDto equalSplitDto) {
        if (expenseService.checkEqualSplitDto(equalSplitDto)) {
            if (expenseService.findById(id).isPresent()) {
                String validationResult = expenseService.equalExpenseValidation(equalSplitDto);
                if (validationResult.equalsIgnoreCase("Success")) {
                    expenseService.editOrRemoveEqualExpense(id, equalSplitDto); //:TODO
                    return new ResponseTemplate(
                            ResponseStatusCode.EQUAL_EXPENSE_EDITED_SUCCESSFULLY.getMessage(),
                            ResponseStatusCode.EQUAL_EXPENSE_EDITED_SUCCESSFULLY.getHttpCode(),
                            validationResult);
                }
                return new ResponseTemplate(
                        ResponseStatusCode.EQUAL_EXPENSE_VALIDATION_FAILED.getMessage(),
                        ResponseStatusCode.EQUAL_EXPENSE_VALIDATION_FAILED.getHttpCode(),
                        validationResult);
            }
            return new ResponseTemplate(
                    ResponseStatusCode.INVALID_EXPENSE_ID.getMessage(),
                    ResponseStatusCode.INVALID_EXPENSE_ID.getHttpCode(),
                    "Invalid Expense Id");
        }
        return new ResponseTemplate(
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getMessage(),
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getHttpCode(),
                "request body incorrect");
    }

    @PostMapping("/create/exact-expense")
    public ResponseTemplate createExactExpense(@RequestBody Expense expense) {
        if (expenseService.checkExpenseObject(expense)) {
            String validationResult = expenseService.exactExpenseValidation(expense);
            if (validationResult.equalsIgnoreCase("Success")) {
                expenseService.createExactExpense(expense, null);
                return new ResponseTemplate(
                        ResponseStatusCode.EXACT_EXPENSE_CREATED.getMessage(),
                        ResponseStatusCode.EXACT_EXPENSE_CREATED.getHttpCode(),
                        validationResult);
            }
            return new ResponseTemplate(
                    ResponseStatusCode.EXACT_EXPENSE_VALIDATION_FAILED.getMessage(),
                    ResponseStatusCode.EXACT_EXPENSE_VALIDATION_FAILED.getHttpCode(),
                    validationResult);
        }
        return new ResponseTemplate(
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getMessage(),
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getHttpCode(),
                "request body incorrect");
    }

    @PutMapping("/edit/exact-expense/{id}")
    public ResponseTemplate editExactExpense(@PathVariable("id") String id, @RequestBody Expense expense) {
        if (expenseService.checkExpenseObject(expense)) {
            if (expenseService.findById(id).isPresent()) {
                String validationResult = expenseService.exactExpenseValidation(expense);
                if (validationResult.equalsIgnoreCase("Success")) {
                    expenseService.editOrRemoveExactExpense(id, expense); //:TODO
                    return new ResponseTemplate(
                            ResponseStatusCode.EXACT_EXPENSE_EDITED_SUCCESSFULLY.getMessage(),
                            ResponseStatusCode.EXACT_EXPENSE_EDITED_SUCCESSFULLY.getHttpCode(),
                            validationResult);
                }
                return new ResponseTemplate(
                        ResponseStatusCode.EXACT_EXPENSE_VALIDATION_FAILED.getMessage(),
                        ResponseStatusCode.EXACT_EXPENSE_VALIDATION_FAILED.getHttpCode(),
                        validationResult);
            }
            return new ResponseTemplate(
                    ResponseStatusCode.INVALID_EXPENSE_ID.getMessage(),
                    ResponseStatusCode.INVALID_EXPENSE_ID.getHttpCode(),
                    "Invalid Expense Id");
        }
        return new ResponseTemplate(
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getMessage(),
                ResponseStatusCode.REQUEST_BODY_INCORRECT.getHttpCode(),
                "request body incorrect");
    }

    @DeleteMapping("/remove/user/{groupId}/{userId}")
    public ResponseTemplate removeUserFromGroup(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId) {
        if (groupService.findByGroupId(groupId).isPresent() && userService.findById(userId).isPresent()) {
            Group group = groupService.findByGroupId(groupId).get();
            group.setId(group.getId());
            Map<String, Double> userMap = group.getCurrentBalance();
            if (userMap.containsKey(userId)) {
                double pendingAmount = userMap.get(userId);
                if (pendingAmount == 0.0) {
                    group.setId(group.getId());
                    userMap.remove(userId);
                    groupService.save(group);
                    return new ResponseTemplate(
                            ResponseStatusCode.USER_REMOVED_FROM_GROUP_SUCCESSFULLY.getMessage(),
                            ResponseStatusCode.USER_REMOVED_FROM_GROUP_SUCCESSFULLY.getHttpCode(),
                            "User Removed from the group");
                } else {
                    if (pendingAmount > 0) {
                        return new ResponseTemplate(
                                ResponseStatusCode.SETTLEMENT_REQUIRED.getMessage(),
                                ResponseStatusCode.SETTLEMENT_REQUIRED.getHttpCode(),
                                new String("Error ! -> Settlement Required " +
                                        "Group owes you : rs " + pendingAmount));
                    }
                    return new ResponseTemplate(
                            ResponseStatusCode.SETTLEMENT_REQUIRED.getMessage(),
                            ResponseStatusCode.SETTLEMENT_REQUIRED.getHttpCode(),
                            new String("Error ! -> Settlement Required " +
                                    "You owe to Group : rs" + pendingAmount));
                }
            }

            return new ResponseTemplate(
                    ResponseStatusCode.USER_NOT_PRESENT_IN_GROUP.getMessage(),
                    ResponseStatusCode.USER_NOT_PRESENT_IN_GROUP.getHttpCode(),
                    "User not present in the group");

        }

        return new ResponseTemplate(
                ResponseStatusCode.INVALID_USER_ID_OR_GROUP_ID.getMessage(),
                ResponseStatusCode.INVALID_USER_ID_OR_GROUP_ID.getHttpCode(),
                "Check User id or Group id");
    }

    @DeleteMapping("/remove/expense/{id}")
    public ResponseTemplate removeExpense(@PathVariable("id") String id) {
        if (expenseService.findById(id).isPresent()) {
            expenseService.editOrRemoveExactExpense(id, null);
            return new ResponseTemplate(
                    ResponseStatusCode.EXPENSE_REMOVED_SUCCESSFULLY.getMessage(),
                    ResponseStatusCode.EXPENSE_REMOVED_SUCCESSFULLY.getHttpCode(),
                    "OK");
        }
        return new ResponseTemplate(
               ResponseStatusCode.INVALID_EXPENSE_ID.getMessage(),
                ResponseStatusCode.INVALID_EXPENSE_ID.getHttpCode(),
                "Invalid Expense Id");
    }
}


