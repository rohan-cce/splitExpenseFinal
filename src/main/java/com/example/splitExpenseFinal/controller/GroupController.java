package com.example.splitExpenseFinal.controller;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.dto.EqualSplitDto;
import com.example.splitExpenseFinal.returnTemplate.ResponseTemplate;
import com.example.splitExpenseFinal.service.ExpenseService;
import com.example.splitExpenseFinal.service.GroupService;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
        if(!group.getName().isEmpty()) {
            Group group1 =  groupService.createGroup(group);

            if (group1 != null) {
                return new ResponseTemplate(
                        HttpStatus.ACCEPTED.getReasonPhrase(),
                        HttpStatus.ACCEPTED.value(),
                        group1);
            }
        }
        return new ResponseTemplate(
                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                HttpStatus.NOT_ACCEPTABLE.value(),
                "group not created");
    }

    @GetMapping("/find/{name}")
    public Group findGroup(@PathVariable("name") String name) {
        return groupService.findByGroup(name);
    }

    @PostMapping("/add/user/{groupId}/{userId}")
    public ResponseTemplate addUserToGroup(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId){
        if(groupService.findByGroupId(groupId).isPresent() && userService.findById(userId).isPresent()) {
            Group group = groupService.findByGroupId(groupId).get();
            group.setId(group.getId());
            Map<String, Double> userMap = group.getCurrentBalance();
            if (!userMap.containsKey(userId)) {
                group.setId(group.getId());
                userMap.put(userId, 0.0);
                groupService.save(group);
                return new ResponseTemplate(
                        HttpStatus.OK.getReasonPhrase(),
                        HttpStatus.OK.value(),
                        "User Added in group");
            }
            return new ResponseTemplate(
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    "User Already present");

        }
        return new ResponseTemplate(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                "Invalid userId or groupId" );
    }

    @PostMapping("/create/equal-expense")
    public ResponseTemplate createEqualExpense(@RequestBody EqualSplitDto equalSplitDto) {
        if (expenseService.checkEqualSplitDto(equalSplitDto)) {
            String validationResult = expenseService.equalExpenseValidation(equalSplitDto);
            if (validationResult.equalsIgnoreCase("Success")) {
                expenseService.createEqualExpense(equalSplitDto, null);
                return new ResponseTemplate(
                        HttpStatus.OK.getReasonPhrase(),
                        HttpStatus.OK.value(),
                        validationResult);
            }
            return new ResponseTemplate(
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    validationResult);
        }
        return new ResponseTemplate(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "request body incorrect");
    }

    @PutMapping("/edit/equal-expense/{id}")
    public ResponseTemplate editEqualExpense(@PathVariable("id") String id,@RequestBody EqualSplitDto equalSplitDto) {
        if (expenseService.checkEqualSplitDto(equalSplitDto)) {
            if (expenseService.findById(id).isPresent()) {
                String validationResult = expenseService.equalExpenseValidation(equalSplitDto);
                if (validationResult.equalsIgnoreCase("Success")) {

                    expenseService.editOrRemoveEqualExpense(id, equalSplitDto); //:TODO
                    //expenseService.editEqualExpense(id,equalSplitDto);
                    return new ResponseTemplate(
                            HttpStatus.OK.getReasonPhrase(),
                            HttpStatus.OK.value(),
                            validationResult);
                }
                return new ResponseTemplate(
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        HttpStatus.NOT_ACCEPTABLE.value(),
                        validationResult);
            }
            return new ResponseTemplate(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    HttpStatus.NOT_FOUND.value(),
                    "Invalid Expense Id");
        }
        return new ResponseTemplate(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "request body incorrect");
    }

    @PostMapping("/create/exact-expense")
    public ResponseTemplate createExactExpense(@RequestBody Expense expense) {
        if (expenseService.checkExpenseObject(expense)) {
            String validationResult = expenseService.exactExpenseValidation(expense);
            if (validationResult.equalsIgnoreCase("Success")) {
                expenseService.createExactExpense(expense, null);
                return new ResponseTemplate(
                        HttpStatus.OK.getReasonPhrase(),
                        HttpStatus.OK.value(),
                        validationResult);
            }
            return new ResponseTemplate(
                    HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                    HttpStatus.NOT_ACCEPTABLE.value(),
                    validationResult);
        }
        return new ResponseTemplate(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "request body incorrect");
    }

    @PutMapping("/edit/exact-expense/{id}")
    public ResponseTemplate editExactExpense(@PathVariable("id") String id,@RequestBody Expense expense) {
        if (expenseService.checkExpenseObject(expense)) {
            if (expenseService.findById(id).isPresent()) {
                String validationResult = expenseService.exactExpenseValidation(expense);
                if (validationResult.equalsIgnoreCase("Success")) {

                    expenseService.editOrRemoveExactExpense(id, expense); //:TODO
                    return new ResponseTemplate(
                            HttpStatus.OK.getReasonPhrase(),
                            HttpStatus.OK.value(),
                            validationResult);
                }
                return new ResponseTemplate(
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        HttpStatus.NOT_ACCEPTABLE.value(),
                        validationResult);
            }
            return new ResponseTemplate(
                    HttpStatus.NOT_FOUND.getReasonPhrase(),
                    HttpStatus.NOT_FOUND.value(),
                    "Invalid Expense Id");
        }
        return new ResponseTemplate(
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value(),
                "request body incorrect");
    }

    @DeleteMapping("/remove/user/{groupId}/{userId}")
    public ResponseTemplate removeUserToGroup(@PathVariable("groupId") String groupId, @PathVariable("userId") String userId){
        if(groupService.findByGroupId(groupId).isPresent() && userService.findById(userId).isPresent()){
            Group group = groupService.findByGroupId(groupId).get();
            group.setId(group.getId());
            Map<String,Double> userMap = group.getCurrentBalance();
            double pendingAmount = userMap.get(userId);
            if(userMap.containsKey(userId)) {
                if (pendingAmount == 0.0) {
                    group.setId(group.getId());
                    userMap.remove(userId);
                    groupService.save(group);
                    return new ResponseTemplate(
                            HttpStatus.OK.getReasonPhrase(),
                            HttpStatus.OK.value(),
                            "User Removed from the group");
                }
                else{
                    if(pendingAmount>0){
                        return new ResponseTemplate(
                                HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                                HttpStatus.NOT_ACCEPTABLE.value(),
                                new String("Error ! -> Settlement Required "+
                                        "Group owes you : rs "+pendingAmount));
                    }
                return new ResponseTemplate(
                        HttpStatus.NOT_ACCEPTABLE.getReasonPhrase(),
                        HttpStatus.NOT_ACCEPTABLE.value(),
                        new String("Error ! -> Settlement Required "+
                                "You owe to Group : rs" +pendingAmount));
                }
            }

        return new ResponseTemplate(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                "User not present in the group" );

        }

        return new ResponseTemplate(
            HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                        "Check User id or Group id" );
    }

    @DeleteMapping("/remove/expense/{id}")
    public ResponseTemplate removeExpense(@PathVariable("id") String id){
        if(expenseService.findById(id).isPresent()){
            expenseService.editOrRemoveExactExpense(id,null);
            return new ResponseTemplate(
                    HttpStatus.OK.getReasonPhrase(),
                    HttpStatus.OK.value(),
                    "OK");
        }
        return new ResponseTemplate(
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                HttpStatus.NOT_FOUND.value(),
                "Invalid Expense Id");

    }
}


