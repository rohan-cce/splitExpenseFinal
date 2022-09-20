package com.example.splitExpenseFinal.service;

import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.dto.EqualSplitDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface GroupService {

    Group createGroup(Group group);

    Group findByGroup(String name);

    Optional<Group> findByGroupId(String id);

    boolean checkUserList(List<String> users, String groupId);

    boolean checkWhetherUserExists(String id);

    void save(Group group);

}
