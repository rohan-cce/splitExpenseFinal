package com.example.splitExpenseFinal.document;

import com.sun.istack.internal.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

@Document
public class Group {
    @Id
    private String id;
    private String name;
    private Map<String, Double> currentBalance = new HashMap<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, Double> getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Map<String, Double> currentBalance) {
        this.currentBalance = currentBalance;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", currentBalance=" + currentBalance +
                '}';
    }
}
