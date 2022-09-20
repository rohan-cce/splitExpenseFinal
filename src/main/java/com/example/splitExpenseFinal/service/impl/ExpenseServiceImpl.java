package com.example.splitExpenseFinal.service.impl;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.dto.EqualSplitDto;
import com.example.splitExpenseFinal.repository.ExpenseRepository;
import com.example.splitExpenseFinal.service.ExpenseService;
import com.example.splitExpenseFinal.service.GroupService;
import com.example.splitExpenseFinal.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExpenseServiceImpl implements ExpenseService {


    @Autowired
    GroupService groupService;

    @Autowired
    UserService userService;

    @Autowired
    ExpenseRepository expenseRepository;


    private Group group;
    private Expense expense;


    @Override
    public boolean checkAmount(ArrayList arrayList, Double amount) {
        double totalAmount = 0;
        for (Object userAmount : arrayList) {
            totalAmount += (double) userAmount;
        }
        if (totalAmount == amount)
            return true;
        return false;
    }


    @Override
    public String equalExpenseValidation(EqualSplitDto equalSplitDto) {
        if (equalSplitDto.getSplitType().equalsIgnoreCase("EQUAL")) {
            if (groupService.findByGroupId(equalSplitDto.getGroupId()).isPresent() && groupService.checkWhetherUserExists(equalSplitDto.getPayeeId())) {
                if (groupService.checkUserList(equalSplitDto.getListOfUsers(), equalSplitDto.getGroupId())) {
                    return "Success";
                }
                return "Userlist is not valid or User is not present in Group";
            }
            return "Groupid or Payeeid is not valid";
        }
        return "Please Enter EQUAL split method";
    }

    @Override
    public void createEqualExpense(EqualSplitDto equalSplitDto, @Nullable String id) {
        expense = new Expense();
        BeanUtils.copyProperties(equalSplitDto, expense);
        if (id != null) {
            expense = expenseRepository.findById(id).get();
            expense.setAmount(equalSplitDto.getAmount());
        }

        Map<String, Double> usersplitAmountMap = expense.getUsersplitAmountMap();
        List<String> userList = equalSplitDto.getListOfUsers();

        group = groupService.findByGroupId(equalSplitDto.getGroupId()).get();
        Map<String, Double> groupMap = group.getCurrentBalance();

        double splitAmount = calculateSplit(equalSplitDto.getAmount(), userList.size());

        User payeeuser = userService.findById(equalSplitDto.getPayeeId()).get();
        payeeuser.setId(equalSplitDto.getPayeeId());

        groupMap.put(payeeuser.getId(), groupMap.get(payeeuser.getId()) + equalSplitDto.getAmount());

        payeeuser.setSpent(payeeuser.getSpent() + equalSplitDto.getAmount());
        payeeuser.setBalance(payeeuser.getBalance() + equalSplitDto.getAmount());
        userService.save(payeeuser);

        for (String s : userList) {
            User user = userService.findById(s).get();
            user.setId(s);
            user.setBalance(user.getBalance() - splitAmount);
            usersplitAmountMap.put(s, splitAmount);
            groupMap.put(user.getId(), groupMap.get(user.getId()) - splitAmount); //:TODO
            userService.save(user);
        }

        expenseRepository.save(expense);
        groupService.save(group);
    }

    @Override
    public String exactExpenseValidation(Expense expense) {
        if (expense.getSplitType().equalsIgnoreCase("EXACT")) {
            if (groupService.findByGroupId(expense.getGroupId()).isPresent() && groupService.checkWhetherUserExists(expense.getPayeeId())) {
                if (groupService.checkUserList(new ArrayList(expense.getUsersplitAmountMap().keySet()), expense.getGroupId())) {
                    if (checkAmount(new ArrayList<Double>(expense.getUsersplitAmountMap().values()), expense.getAmount())) {
                        return "Success";
                    }
                    return "total amount not matching with split";
                }
                return "Usermap is not valid or User is not present in Group";
            }
            return "Groupid or Payeeid is not valid";
        }
        return "Please Enter EXACT split method";
    }

    @Override
    public Optional<Expense> findById(String id) {
        return expenseRepository.findById(id);
    }

    @Override
    public void editOrRemoveEqualExpense(String id, @Nullable EqualSplitDto equalSplitDto) {
        expense = expenseRepository.findById(id).get();
        expense.setId(id);

        Map<String, Double> usersplitAmountMap = expense.getUsersplitAmountMap();
        List<String> userList = equalSplitDto.getListOfUsers();

        group = groupService.findByGroupId(expense.getGroupId()).get();
        Map<String, Double> groupMap = group.getCurrentBalance();

        double splitAmount = calculateSplit(expense.getAmount(), userList.size());

        User payeeuser = userService.findById(expense.getPayeeId()).get();
        payeeuser.setId(expense.getPayeeId());
        payeeuser.setSpent(payeeuser.getSpent() - expense.getAmount());
        payeeuser.setBalance(payeeuser.getBalance() - expense.getAmount());
        groupMap.put(payeeuser.getId(), groupMap.get(payeeuser.getId()) - expense.getAmount());
        userService.save(payeeuser);

        expense.setAmount(0.0);
        for (String s : userList) {
            User user = userService.findById(s).get();
            user.setId(s);
            user.setBalance(user.getBalance() + splitAmount);
            usersplitAmountMap.put(s, usersplitAmountMap.get(s) - splitAmount);
            groupMap.put(s, groupMap.get(s) + splitAmount);
            userService.save(user);
        }
        expenseRepository.save(expense);
        groupService.save(group);

        if (equalSplitDto != null) {
            createEqualExpense(equalSplitDto, id);
        } else {
            removeExpense(id);
        }
    }

    @Override
    public void editOrRemoveExactExpense(String id, @Nullable Expense editExpense) {

        expense = expenseRepository.findById(id).get();
        Map<String, Double> usersplitAmountMap = expense.getUsersplitAmountMap();
        group = groupService.findByGroupId(expense.getGroupId()).get();
        Map<String, Double> groupMap = group.getCurrentBalance();
        User payeeuser = userService.findById(expense.getPayeeId()).get();
        payeeuser.setId(expense.getPayeeId());
        groupMap.put(payeeuser.getId(), groupMap.get(payeeuser.getId()) - expense.getAmount());
        payeeuser.setSpent(payeeuser.getSpent() - expense.getAmount());
        payeeuser.setBalance(payeeuser.getBalance() - expense.getAmount());
        userService.save(payeeuser);

        for (String s : usersplitAmountMap.keySet()) {

            User user = userService.findById(s).get();
            user.setId(user.getId());
            user.setBalance(user.getBalance() + usersplitAmountMap.get(user.getId()));
            double amount = usersplitAmountMap.get(user.getId());
            usersplitAmountMap.put(user.getId(), amount);
            groupMap.put(user.getId(), groupMap.get(user.getId()) + usersplitAmountMap.get(s));
            userService.save(user);
        }


        expenseRepository.save(expense);
        groupService.save(group);

        if (editExpense != null) {
            createExactExpense(editExpense, id);
        } else {
            removeExpense(id);
        }

    }


    @Override
    public void createExactExpense(Expense expense, @Nullable String id) {
        if (id != null) {
            expense.setId(id);
        }

        Map<String, Double> usersplitAmountMap = expense.getUsersplitAmountMap();
        group = groupService.findByGroupId(expense.getGroupId()).get();
        Map<String, Double> groupMap = group.getCurrentBalance();
        User payeeuser = userService.findById(expense.getPayeeId()).get();
        payeeuser.setId(expense.getPayeeId());
        groupMap.put(payeeuser.getId(), groupMap.get(payeeuser.getId()) + expense.getAmount());
        payeeuser.setSpent(payeeuser.getSpent() + expense.getAmount());
        payeeuser.setBalance(payeeuser.getBalance() + expense.getAmount());
        userService.save(payeeuser);

        for (String s : usersplitAmountMap.keySet()) {

            User user = userService.findById(s).get();
            user.setId(user.getId());
            user.setBalance(user.getBalance() - usersplitAmountMap.get(user.getId()));
            double amount = usersplitAmountMap.get(user.getId());
            usersplitAmountMap.put(user.getId(), amount);
            groupMap.put(user.getId(), groupMap.get(user.getId()) - usersplitAmountMap.get(s));
            userService.save(user);
        }


        expenseRepository.save(expense);
        groupService.save(group);

    }

    @Override
    public boolean checkExpenseObject(Expense expense) {
        if (expense.getDescription() != null &&
                expense.getSplitType() != null &&
                expense.getAmount() != null &&
                expense.getUsersplitAmountMap() != null &&
                expense.getGroupId() != null &&
                expense.getPayeeId() != null
                ) {
            return true;
        }
        return false;
    }

    @Override
    public void removeExpense(String id) {
        expenseRepository.deleteById(id);
    }

    @Override
    public boolean checkEqualSplitDto(EqualSplitDto equalSplitDto) {
        if (equalSplitDto.getDescription() != null &&
                equalSplitDto.getSplitType() != null &&
                equalSplitDto.getAmount() != null &&
                equalSplitDto.getListOfUsers() != null &&
                equalSplitDto.getGroupId() != null &&
                equalSplitDto.getPayeeId() != null
                ) {
            return true;
        }
        return false;
    }


    public double calculateSplit(double amount, int users) {
        return amount / users;
    }
}
