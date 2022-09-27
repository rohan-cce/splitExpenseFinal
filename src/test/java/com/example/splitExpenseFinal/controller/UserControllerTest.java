package com.example.splitExpenseFinal.controller;

import com.example.splitExpenseFinal.document.User;
import com.example.splitExpenseFinal.repository.UserRepository;
import com.example.splitExpenseFinal.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.is;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();
    ObjectWriter objectWriter = objectMapper.writer();

    @Mock
    private UserService userService;


    @InjectMocks
    private UserController userController;


    User USER_1 = new User("1","user1");
    User USER_2 = new User("2","user2");
    User EMPTY_USER = new User("3","");



    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build(); //mock user repo

        List<User> userList = Arrays.asList(USER_1,USER_2);
    }

    @Test
    public void createUserSuccessTest() throws Exception{

        User sampleUser = User.builder()
                .name("dummy")
                .build();

        Mockito.when(userService.createUser(sampleUser)).thenReturn(sampleUser);
        String dummyUserString = objectWriter.writeValueAsString(sampleUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(dummyUserString);

        mockMvc.perform(mockRequest)
//                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is("Created")))
                .andExpect(jsonPath("$.code",is(201)))
                .andExpect(jsonPath("$.value.name",is("sample")));
    }



    @Test
    public void createUserFailureTest() throws Exception{

        User emptyUser = User.builder()
                .name("")
                .build();

        Mockito.when(userService.createUser(emptyUser)).thenReturn(emptyUser);

        String emptyUserString = objectWriter.writeValueAsString(emptyUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user/create")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(emptyUserString);

        mockMvc.perform(mockRequest)
                .andExpect(status().isOk())
//                .andExpect(jsonPath("$",notNullValue()))
                .andExpect(jsonPath("$.status",is("Not Acceptable")))
                .andExpect(jsonPath("$.code",is(406)))
                .andExpect(jsonPath("$.value",is("user not created")));
    }


    @Test
    public void showUserBalanceUserIdDoesntExistTest() throws Exception{
        User dummyUser = User.builder()
                .name("dummy")
                .build();

        Mockito.when(userService.findById("3")).thenReturn(Optional.ofNullable(null));
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user/show-balance/{id}",3)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("Not Found")))
                .andExpect(jsonPath("$.code",is(404)))
                .andExpect(jsonPath("$.value",is("User id doesnt exist")));


    }

    @Test
    public void showUserBalanceUserNeedsToGetBalanceTest() throws Exception{
        User dummyUser = User.builder()
                .id("1")
                .name("dummy")
                .balance(20.00)
                .build();

        Mockito.when(userService.findById(dummyUser.getId())).thenReturn(Optional.ofNullable(dummyUser));
        Mockito.when(userService.showUserBalance(dummyUser.getId())).thenReturn(dummyUser.getBalance());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user/show-balance/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("user need to get : rs 20.0")));

    }

    @Test
    public void showUserBalanceUserOwesBalanceTest() throws Exception{
        User dummyUser = User.builder()
                .id("1")
                .name("dummy")
                .balance(-20.00)
                .build();

        Mockito.when(userService.findById(dummyUser.getId())).thenReturn(Optional.ofNullable(dummyUser));
        Mockito.when(userService.showUserBalance(dummyUser.getId())).thenReturn(dummyUser.getBalance());

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/user/show-balance/{id}",1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("user owes : rs -20.0")));

    }


}
