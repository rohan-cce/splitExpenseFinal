package com.example.splitExpenseFinal.repository;

import com.example.splitExpenseFinal.document.Group;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    Group save(Group group);

    Group findByName(String name);

    Optional<Group> findById(String id);
}
