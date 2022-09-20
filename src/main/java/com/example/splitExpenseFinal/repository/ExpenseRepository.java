package com.example.splitExpenseFinal.repository;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {
    Expense save(Expense expense);

    Optional<Expense> findById(String id);

    void deleteById(String id);

}
