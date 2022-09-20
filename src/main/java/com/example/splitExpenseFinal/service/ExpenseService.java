package com.example.splitExpenseFinal.service;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.dto.EqualSplitDto;
import org.springframework.lang.Nullable;

import java.util.ArrayList;
import java.util.Optional;

public interface ExpenseService {

    void createEqualExpense(EqualSplitDto equalSplitDto,@Nullable String id);

    boolean checkAmount(ArrayList arrayList, Double amount);

    void createExactExpense(Expense expense, @Nullable String id);

    String equalExpenseValidation(EqualSplitDto equalSplitDto);

    String exactExpenseValidation(Expense expense);

    Optional<Expense> findById(String id);

    void editOrRemoveEqualExpense(String id, @Nullable EqualSplitDto equalSplitDto);
    void editOrRemoveExactExpense(String id,@Nullable Expense editExpense);

     boolean checkEqualSplitDto(EqualSplitDto equalSplitDto);
     boolean checkExpenseObject(Expense expense);

     void removeExpense(String id);
}
