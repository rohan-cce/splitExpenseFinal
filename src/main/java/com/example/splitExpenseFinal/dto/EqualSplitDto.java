package com.example.splitExpenseFinal.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EqualSplitDto {

    private String description;
    private String splitType;
    private Double amount;
    private List<String> listOfUsers;
    private String groupId;
    private String payeeId;

}
