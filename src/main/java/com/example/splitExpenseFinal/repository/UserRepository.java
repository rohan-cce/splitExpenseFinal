package com.example.splitExpenseFinal.repository;

import com.example.splitExpenseFinal.document.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User,String> {
    User save(User user);

    Optional<User> findById(String id);


}
