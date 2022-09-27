package com.example.splitExpenseFinal.controller;


import com.example.splitExpenseFinal.document.Group;
import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.service.ExpenseService;
import com.example.splitExpenseFinal.service.GroupService;
import com.example.splitExpenseFinal.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
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

import javax.jws.soap.SOAPBinding;
import java.util.*;

import static org.hamcrest.Matchers.is;
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

    private final String CREATE_GROUP_URL = "/group/create";
    private final String ADD_USER_TO_GROUP_URL = "/group/add/user/{groupId}/{userId}";

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

        String emptyUserString = objectWriter.writeValueAsString(sampleGroup);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_GROUP_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(emptyUserString);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("group not created")));

    }

//


}
