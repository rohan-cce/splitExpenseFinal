package com.example.splitExpenseFinal.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Map;

@Document(collection = "expense")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Expense {
    @Id
    private String id;
    private String description;
    private String splitType;
    private Double amount;
    private Map<String, Double> userSplitAmountMap = new HashMap<>();
    private String groupId;
    private String payeeId;

}
