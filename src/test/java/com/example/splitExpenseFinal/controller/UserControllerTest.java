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

    private final String CREATE_USER_URL = "/user/create";
    private final String SHOW_USER_BALANCE_URL = "/user/show-balance/{id}";


    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(userController).build(); //mock user repo

        List<User> userList = Arrays.asList(USER_1,USER_2);
    }

    @Test
    public void createUserSuccessTest() throws Exception{

        User sampleUser = User.builder()
                .name("sample")
                .build();

        Mockito.when(userService.createUser(sampleUser)).thenReturn(sampleUser);
        String dummyUserString = objectWriter.writeValueAsString(sampleUser);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_USER_URL)
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

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(CREATE_USER_URL)
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
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(SHOW_USER_BALANCE_URL,3)
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

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(SHOW_USER_BALANCE_URL,1)
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

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post(SHOW_USER_BALANCE_URL,1)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc.perform(mockRequest)
                .andExpect(jsonPath("$.status",is("OK")))
                .andExpect(jsonPath("$.code",is(200)))
                .andExpect(jsonPath("$.value",is("user owes : rs -20.0")));

    }













//    @Test
//    public void createUserSuccessTest(){
//        when(userRepository.save(user1)).thenReturn(user1);
//        ResponseTemplate responseTemplate = userController.createUser(user1);
//        assertEquals("status","Created",responseTemplate.getStatus());
//        assertEquals("code",201,responseTemplate.getCode());
//        assertEquals("user object",user1,responseTemplate.getValue());
//    }
//
//    @Test
//    public void createUserFailureTest(){
//        User user = new User("");
//        when(userRepository.save(user)).thenReturn(user);
//        ResponseTemplate responseTemplate = userController.createUser(user);
//        assertEquals("status","Not Acceptable",responseTemplate.getStatus());
//        assertEquals("code",406,responseTemplate.getCode());
//        assertEquals("user not created","user not created",responseTemplate.getValue());
//    }
//
//    @Test
//    public void showUserBalanceSuccessTest(){
//
//        when(userRepository.save(user1)).thenReturn(user1);
//        String id = user1.getId();
//
//        Optional<User> userOptional = Optional.of(user1);
//        when(userService.findById(id)).thenReturn(userOptional);
//
//        System.out.println(user1);
////        when(userRepository.findById(id).get().getBalance()).thenReturn(0.0);
//        ResponseTemplate responseTemplate = userController.showUserBalance(user1.getId());
//        System.out.println(responseTemplate);
//
//    }


}
