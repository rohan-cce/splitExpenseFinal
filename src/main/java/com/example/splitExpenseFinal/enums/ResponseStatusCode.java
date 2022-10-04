package com.example.splitExpenseFinal.enums;

public enum ResponseStatusCode {


    SUCCESS(
            200,
            "Success"),
    USER_ADDED_IN_GROUP(
            200,
            "user added in group"
    ),
    USER_NEEDS_TO_GET_AMOUNT(
            200,
            "User needs to get amount"),
    EQUAL_EXPENSE_EDITED_SUCCESSFULLY(
            200,
            "Equal Expense Edited"
    ),
    EXACT_EXPENSE_EDITED_SUCCESSFULLY(
            200,
            "Exact Expense Edited"
    ),
    EXPENSE_REMOVED_SUCCESSFULLY(
            200,
            "Expense Removed Successfully"
    ),
    USER_REMOVED_FROM_GROUP_SUCCESSFULLY(
            200,
            "User Removed From Group Successfully"
    ),
    USER_OWES_AMOUNT(
            200,
            "User owes amount"),
    USER_CREATED(
            201,
            "User Created"
    ),
    GROUP_CREATED(
            201,
            "Group Created"
    ),
    EQUAL_EXPENSE_CREATED(
            201,
            "Equal Expense Created"
    ),
    EXACT_EXPENSE_CREATED(
            201,
            "Exact Expense Created"
    ),
    CREATED(
            201,
            "Created"),
    BAD_REQUEST(
            400,
            "Bad Request"),
    EMPTY_USER_NAME(
            400,
            "Empty User Name"
        ),
    EMPTY_GROUP_NAME(
            400,
            "Empty Group Name"
    ),
    REQUEST_BODY_INCORRECT(
            400,
            "Request Body Incorrect"
    ),
    SETTLEMENT_REQUIRED(
            403,
            "Settlement required"
    ),
    USER_ID_DOESNT_EXIST(
            404,
            "User id doesnt exist"
    ),
    INVALID_EXPENSE_ID(
            404,
            "Invalid Expense Id"
    ),
    INVALID_USER_ID_OR_GROUP_ID(
            404,
            "Invalid User Id or Group Id"
    ),
    USER_NOT_PRESENT_IN_GROUP(
            404,
            "User Not Present in Group"
    ),
    USER_ALREADY_PRESENT_IN_GROUP(
            406,
            "User already present in group"
    ),
    EQUAL_EXPENSE_VALIDATION_FAILED(
            406,
            "Equal Expense Validation Failed"
    ),
    EXACT_EXPENSE_VALIDATION_FAILED(
            406,
            "Exact Expense Validation Failed"
    )
    ;



    private final int httpCode;
    private final String message;

    ResponseStatusCode(int httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }

    public int getHttpCode() {
        return httpCode;
    }

    public String getMessage() {
        return message;
    }
}
