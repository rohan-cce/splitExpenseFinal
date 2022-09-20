package com.example.splitExpenseFinal.document;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document
public class Expense {
    @Id
    private String id;
    private String description;
    private String splitType;
    private Double amount;
    private Map<String, Double> usersplitAmountMap = new HashMap<>();
    private String groupId;
    private String payeeId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSplitType() {
        return splitType;
    }

    public void setSplitType(String splitType) {
        this.splitType = splitType;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Map<String, Double> getUsersplitAmountMap() {
        return usersplitAmountMap;
    }

    public void setUsersplitAmountMap(Map<String, Double> usersplitAmountMap) {
        this.usersplitAmountMap = usersplitAmountMap;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getPayeeId() {
        return payeeId;
    }

    public void setPayeeId(String payeeId) {
        this.payeeId = payeeId;
    }

    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", splitType='" + splitType + '\'' +
                ", amount=" + amount +
                ", usersplitAmountMap=" + usersplitAmountMap +
                ", groupId='" + groupId + '\'' +
                ", payeeId='" + payeeId + '\'' +
                '}';
    }
}
