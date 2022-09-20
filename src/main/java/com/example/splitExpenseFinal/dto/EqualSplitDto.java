package com.example.splitExpenseFinal.dto;

import javax.validation.constraints.NotNull;
import java.util.List;


public class EqualSplitDto {

    private String description;
    private String splitType;
    private Double amount;
    private List<String> listOfUsers;
    private String groupId;
    private String payeeId;

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

    public List<String> getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfUsers(List<String> listOfUsers) {
        this.listOfUsers = listOfUsers;
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
        return "EqualSplitDto{" +
                ", description='" + description + '\'' +
                ", splitType='" + splitType + '\'' +
                ", amount=" + amount +
                ", listOfUsers=" + listOfUsers +
                ", groupId='" + groupId + '\'' +
                ", payeeId='" + payeeId + '\'' +
                '}';
    }
}
