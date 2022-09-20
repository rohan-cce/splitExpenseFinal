package com.example.splitExpenseFinal.service.impl;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.repository.ExpenseRepository;
import com.example.splitExpenseFinal.repository.GroupRepository;
import com.example.splitExpenseFinal.repository.UserRepository;
import com.example.splitExpenseFinal.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class GroupServiceImpl implements GroupService {


    @Autowired
    GroupRepository groupRepository;


    @Autowired
    UserRepository userRepository;

    @Override
    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group findByGroup(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public Optional<Group> findByGroupId(String id) {
        return groupRepository.findById(id);
    }

    @Override
    public boolean checkWhetherUserExists(String id) {
        if (userRepository.findById(id).isPresent()) {
            return true;
        }
        return false;
    }

    @Override
    public void save(Group group) {
        groupRepository.save(group);
    }

    @Override
    public boolean checkUserList(List<String> users, String groupId) {
        Map<String, Double> userMap = groupRepository.findById(groupId).get().getCurrentBalance();
        for (String s : users) {
            if (!checkWhetherUserExists(s) || !userMap.containsKey(s)) {
                return false;
            }
        }
        return true;
    }

}
