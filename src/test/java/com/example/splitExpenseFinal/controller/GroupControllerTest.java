package com.example.splitExpenseFinal.controller;


import com.example.splitExpenseFinal.document.Expense;
import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.dto.EqualSplitDto;
import com.example.splitExpenseFinal.service.ExpenseService;
import com.example.splitExpenseFinal.service.GroupService;
import com.example.splitExpenseFinal.service.UserService;
import com.example.splitExpenseFinal.service.impl.ExpenseServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sun.tools.corba.se.idl.constExpr.Equal;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GroupControllerTest {
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private GroupService groupService;

    @Mock
    private ExpenseService expenseService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GroupController groupController;

    Group GROUP_1 = new Group("group1");
    Group GROUP_2 = new Group("group2");

    User USER_1 = new User("1","user1");
    User USER_2 = new User("2","user2");

    private final String CREATE_GROUP_URL = "/group/create";
    private final String ADD_USER_TO_GROUP_URL = "/group/add/user/{groupId}/{userId}";
    private final String CREATE_EQUAL_EXPENSE_URL = "/group/create/equal-expense";
    private final String CREATE_EXACT_EXPENSE_URL = "/group/create/exact-expense";
    private final String REMOVE_USER_FROM_GROUP_URL = "/group/remove/user/{groupId}/{userId}";
    private final String EDIT_EXACT_EXPENSE_URL = "/group/edit/exact-expense/{id}";
    private final String EDIT_EQUAL_EXPENSE_URL = "/group/edit/equal-expense/{id}";
    private final String REMOVE_EXPENSE_URL = "/group/remove/expense/{id}";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(groupController).build(); //mock user repo

        List<Group> groupList = Arrays.asList(GROUP_1,GROUP_2);
    }

    @Test
    public void createGroupSuccessTest() throws Exception{
        Group sampleGroup = Group.builder()
                .name("sample")
                .build();
        Mockito.when(groupService.createGroup(sampleGroup)).thenReturn(sampleGroup);

        String sampleGroupString = objectWriter.writeValueAsString(sampleGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_GROUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(sampleGroupString);

        mockMvc.perform(mockRequest)
//                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("Accepted")))
                .andExpect(jsonPath("$.code",is(202)))
                .andExpect(jsonPath("$.value.name",is("sample")));


    }

    @Test
    public void createGroupFailureTest() throws Exception{
        Group sampleGroup = Group.builder()
                .name("")
                .build();
        Mockito.when(groupService.createGroup(sampleGroup)).thenReturn(sampleGroup);

        String emptyGroupString = objectWriter.writeValueAsString(sampleGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_GROUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(emptyGroupString);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("group not created")));

    }

//

    @Test
    public void addUserToGroupInvalidGroupIdTest() throws Exception{
        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .build();
        User sampleUser = User.builder()
            .id("1")
            .name("user1")
            .build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ADD_USER_TO_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.ofNullable(null));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Invalid userId or groupId")));


    }

    @Test
    public void addUserToGroupInvalidUserIdTest() throws Exception{
        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .build();
        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ADD_USER_TO_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.ofNullable(null));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Invalid userId or groupId")));


    }


    @Test
    public void addUserToGroupUserAlreadyPresentTest() throws Exception{
        Map<String,Double> stringDoubleMap = new HashMap<String,Double>(){{
                put("1", 10.0);
            }};
        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(stringDoubleMap)
                .build();

        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ADD_USER_TO_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.ofNullable(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("User Already present")));

    }

    @Test
    public void addUserToGroupUserSuccessTest() throws Exception{
        Map<String,Double> stringDoubleMap = new HashMap<String,Double>(){{
            put("2", 10.0);
        }};
        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(stringDoubleMap)
                .build();

        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(ADD_USER_TO_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.ofNullable(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("User Added in group")));

    }

    @Test
    public void createEqualExpenseSuccessTest() throws Exception{

        List<String> userList = Arrays.asList("1","2","3");

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();
        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Success");

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).createEqualExpense(equalSplitDto,null);

        expenseService.createEqualExpense(equalSplitDto,null);
        verify(expenseService,times(1)).createEqualExpense(equalSplitDto,null);

        String equalSplitDtoString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EQUAL_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(equalSplitDtoString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("Success")));

    }

    @Test
    public void createEqualExpenseCheckEqualSplitDtoFailureTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
//                .amount(10.0) - wantedly commented out amount to check equal split dto validation
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();
        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(false);
        String equalSplitDtoString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EQUAL_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(equalSplitDtoString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Bad Request")))
                .andExpect(jsonPath("$.code",is(400)))
                .andExpect(jsonPath("$.value",is("request body incorrect")));


    }

    @Test
    public void createEqualExpenseWrongSplitMethodTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EXACT")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();
        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Please Enter EQUAL split method");

        String equalSplitDtoString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EQUAL_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(equalSplitDtoString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Please Enter EQUAL split method")));

    }


    @Test
    public void createEqualExpenseWrongPayeeIdOrGroupIdTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("-1")
                .payeeId("2")
                .build();
        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Groupid or Payeeid is not valid");

        String equalSplitDtoString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EQUAL_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(equalSplitDtoString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Groupid or Payeeid is not valid")));

    }

    @Test
    public void createEqualExpenseWrongUserListTest() throws Exception{

        List<String> userList = Arrays.asList("1","2","3");

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();
        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Userlist is not valid or User is not present in Group");

        String equalSplitDtoString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EQUAL_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(equalSplitDtoString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Userlist is not valid or User is not present in Group")));

    }


    @Test
    public void createExactExpenseSuccessTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("EXACT")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Success");

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).createExactExpense(expense,null);

        expenseService.createExactExpense(expense,null);
        verify(expenseService,times(1)).createExactExpense(expense,null);

        String expenseString = objectWriter.writeValueAsString(expense);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("Success")));

    }


    @Test
    public void createExactExpenseCheckExpenseObjectFailureTest() throws Exception{


        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("Exact")
                .amount(30.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(false);

        String expenseString = objectWriter.writeValueAsString(expense);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Bad Request")))
                .andExpect(jsonPath("$.code",is(400)))
                .andExpect(jsonPath("$.value",is("request body incorrect")));


    }

    @Test
    public void createExactExpenseSumOfIndividualAmountsNotMatchingWithTotalAmount() throws Exception{
        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("Exact")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("total amount not matching with split");

        String expenseString = objectWriter.writeValueAsString(expense);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("total amount not matching with split")));

    }


    @Test
    public void createExactExpenseWrongUserMapTest() throws Exception{
        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("5",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("Exact")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Usermap is not valid or User is not present in Group");

        String expenseString = objectWriter.writeValueAsString(expense);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Usermap is not valid or User is not present in Group")));

    }

    @Test
    public void createExactExpenseWrongPayeeIdOrGroupIdTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("Exact")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Groupid or Payeeid is not valid");

        String expenseString = objectWriter.writeValueAsString(expense);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Groupid or Payeeid is not valid")));

    }

    @Test
    public void createExactExpenseWrongSplitMethodTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .description("exact split")
                .splitType("Exacts")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Please Enter EXACT split method");

        String expenseString = objectWriter.writeValueAsString(expense);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_EXACT_EXPENSE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Please Enter EXACT split method")));

    }

    @Test
    public void removeUserFromGroupSuccessTest() throws Exception{

        Map<String,Double> balanceMap = new HashMap<String,Double>(){{
            put("1",0.00);
            put("2",10.00);
        }};

        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(balanceMap)
                .build();
        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_USER_FROM_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("User Removed from the group")));
    }

    @Test
    public void removeUserFromGroupNegativeBalanceTest() throws Exception{

        Map<String,Double> balanceMap = new HashMap<String,Double>(){{
            put("1",-10.00);
            put("2",10.00);
        }};

        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(balanceMap)
                .build();
        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_USER_FROM_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Error ! -> Settlement Required You owe to Group : rs-10.0")));
    }

    @Test
    public void removeUserFromGroupPositiveBalanceTest() throws Exception{

        Map<String,Double> balanceMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",-10.00);
        }};

        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(balanceMap)
                .build();
        User sampleUser = User.builder()
                .id("1")
                .name("user1")
                .build();
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_USER_FROM_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Error ! -> Settlement Required Group owes you : rs 10.0")));
    }

    @Test
    public void removeUserFromGroupUserNotPresentInGroupTest() throws Exception{

        Map<String,Double> balanceMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",-10.00);
        }};

        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(balanceMap)
                .build();

        User sampleUser = User.builder()
                .id("3")
                .name("user1")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_USER_FROM_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.of(sampleGroup));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("User not present in the group")));
    }

    @Test
    public void removeUserFromGroupCheckUserIdOrGroupIdTest() throws Exception{

        Map<String,Double> balanceMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",-10.00);
        }};

        Group sampleGroup = Group.builder()
                .id("1")
                .name("group1")
                .currentBalance(balanceMap)
                .build();

        User sampleUser = User.builder()
                .id("3")
                .name("user1")
                .build();

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_USER_FROM_GROUP_URL,sampleGroup.getId(),sampleUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        Mockito.when(groupService.findByGroupId(sampleGroup.getId())).thenReturn(Optional.ofNullable(null));
        Mockito.when(userService.findById(sampleUser.getId())).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Check User id or Group id")));
    }

    @Test
    public void editExactExpenseSuccessTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("exact split")
                .splitType("EXACT")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Success");
        Mockito.when(expenseService.findById(expense.getId())).thenReturn(Optional.of(expense));

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).createExactExpense(expense,"1");

        expenseService.createExactExpense(expense,"1");
        verify(expenseService,times(1)).createExactExpense(expense,"1");

        String expenseString = objectWriter.writeValueAsString(expense);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EXACT_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);


        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("Success")));

    }

    @Test
    public void editExactExpenseValidationFailureTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("exact split")
                .splitType("EXACT")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("total amount not matching with split");
        Mockito.when(expenseService.findById(expense.getId())).thenReturn(Optional.of(expense));

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).createExactExpense(expense,"1");

        expenseService.createExactExpense(expense,"1");
        verify(expenseService,times(1)).createExactExpense(expense,"1");

        String expenseString = objectWriter.writeValueAsString(expense);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EXACT_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);


        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("total amount not matching with split")));

    }

    @Test
    public void editExactExpenseInvalidExpenseIdTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("exact split")
                .splitType("EXACT")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(true);
        Mockito.when(expenseService.exactExpenseValidation(expense)).thenReturn("Success");
        Mockito.when(expenseService.findById(expense.getId())).thenReturn(Optional.ofNullable(null));


        String expenseString = objectWriter.writeValueAsString(expense);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EXACT_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);


        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Invalid Expense Id")));

    }

    @Test
    public void editExactExpenseIncorrectRequestBodyTest() throws Exception{

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",10.00);
            put("2",12.00);
            put("3",8.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("exact split")
                .splitType("EXACT")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkExpenseObject(expense)).thenReturn(false);



        String expenseString = objectWriter.writeValueAsString(expense);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EXACT_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);


        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Bad Request")))
                .andExpect(jsonPath("$.code",is(400)))
                .andExpect(jsonPath("$.value",is("request body incorrect")));

    }


    @Test
    public void editEqualExpenseSuccessTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        String id = "1";

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",8.00);
            put("2",2.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Success");
        Mockito.when(expenseService.findById(id)).thenReturn(Optional.of(expense));

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).editOrRemoveEqualExpense("1",equalSplitDto);

        expenseService.editOrRemoveEqualExpense("1",equalSplitDto);
        verify(expenseService,times(1)).editOrRemoveEqualExpense("1",equalSplitDto);

        String expenseString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EQUAL_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("Success")));

    }


    @Test
    public void editEqualExpenseValidationFailedTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        String id = "1";

        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",8.00);
            put("2",2.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUALs")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.equalExpenseValidation(equalSplitDto)).thenReturn("Please Enter EQUAL split method");
        Mockito.when(expenseService.findById(id)).thenReturn(Optional.of(expense));


        String expenseString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EQUAL_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("Please Enter EQUAL split method")));

    }


    @Test
    public void editEqualExpenseIdDoesntExistTest() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        String id = "1";


        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(true);
        Mockito.when(expenseService.findById(id)).thenReturn(Optional.ofNullable(null));


        String expenseString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EQUAL_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Invalid Expense Id")));

    }

    @Test
    public void editEqualExpenseIncorrectRequestBody() throws Exception{

        List<String> userList = Arrays.asList("1","2");

        String id = "1";



        EqualSplitDto equalSplitDto = EqualSplitDto.builder()
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .listOfUsers(userList)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.checkEqualSplitDto(equalSplitDto)).thenReturn(false);


        String expenseString = objectWriter.writeValueAsString(equalSplitDto);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put(EDIT_EQUAL_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(expenseString);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Bad Request")))
                .andExpect(jsonPath("$.code",is(400)))
                .andExpect(jsonPath("$.value",is("request body incorrect")));

    }

    @Test
    public void removeExpenseSuccessTest() throws Exception{


        Mockito.when(expenseService.findById("1")).thenReturn(Optional.ofNullable(null));


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("Invalid Expense Id")));


    }

    @Test
    public void removeExpenseFailureTest() throws Exception{
        Map<String,Double> userMap = new HashMap<String,Double>(){{
            put("1",8.00);
            put("2",2.00);
        }
        };

        Expense expense = Expense.builder()
                .id("1")
                .description("equal split")
                .splitType("EQUAL")
                .amount(10.0)
                .usersplitAmountMap(userMap)
                .groupId("1")
                .payeeId("2")
                .build();

        Mockito.when(expenseService.findById(expense.getId())).thenReturn(Optional.of(expense));

        ExpenseService expenseService = mock(ExpenseService.class);
        doNothing().when(expenseService).editOrRemoveExactExpense(expense.getId(),null);

        expenseService.editOrRemoveExactExpense(expense.getId(),null);
        verify(expenseService,times(1)).editOrRemoveExactExpense(expense.getId(),null);


        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.delete(REMOVE_EXPENSE_URL,"1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("OK")));


    }


}
