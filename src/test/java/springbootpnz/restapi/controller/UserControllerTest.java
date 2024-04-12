package springbootpnz.restapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.RegisterUserRequest;
import springbootpnz.restapi.model.UpdateUserRequest;
import springbootpnz.restapi.model.UserResponse;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.MockMvcBuilder.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void testRegisterSuccess() throws Exception {

        RegisterUserRequest request= new RegisterUserRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        request.setName("Joko Anwar");
        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertEquals("OK", response.getData());
        });
    }


    @Test
    void testRegisterFailed() throws Exception {

        RegisterUserRequest request= new RegisterUserRequest();
        request.setUsername("testUser");
        request.setName("Joko Anwar");
        mockMvc.perform(
                post("/api/users/register")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setName("Joko Anwar");
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);
        userRepository.save(user);

        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testToken")
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {});

            assertNull(response.getErrors());

            assertNotNull(response.getData());
            assertEquals(response.getData().getUsername(), user.getUsername());
            assertEquals(response.getData().getName() , user.getName());

        });
    }

    @Test
    void getUserInvalidToken() throws Exception {
        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "notfound")
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void getUserTokenNotFound() throws Exception {
        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }


    @Test
    void getUserTokenExpired() throws Exception {

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("password");
        user.setName("Joko Anwar");
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() - 100000000L);
        userRepository.save(user);


        mockMvc.perform(
                get("/api/users")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }


    @Test
    void updateUserFailed() throws Exception {
        UpdateUserRequest request = new UpdateUserRequest();
        mockMvc.perform(
                patch("/api/users/update").accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }




    @Test
    void updateNameUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(BCrypt.hashpw("testPassword", BCrypt.gensalt()));
        user.setName("Joko Anwar");

        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);

        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Update Testing");

        mockMvc.perform(
                patch("/api/users/update").accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testToken")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {});

            assertEquals(request.getName(), response.getData().getName());
            assertEquals(user.getUsername(), response.getData().getUsername());
            assertNull(response.getErrors());
        });
    }

    @Test
    void updateUserSuccess() throws Exception {
        User user = new User();
        user.setUsername("testUser");
        user.setPassword(BCrypt.hashpw("testPassword", BCrypt.gensalt()));
        user.setName("Joko Anwar");

        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 100000000L);

        userRepository.save(user);

        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Update Testing");
        request.setPassword("0987654321");

        mockMvc.perform(
                patch("/api/users/update").accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", "testToken")
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<UserResponse> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<UserResponse>>() {});

            assertEquals(request.getName(), response.getData().getName());
            assertEquals(user.getUsername(), response.getData().getUsername());
            assertNull(response.getErrors());

            User userDb = userRepository.findById(response.getData().getUsername()).orElse(null);
            assertNotNull(userDb);
            assert request.getPassword() != null;
            assertTrue(BCrypt.checkpw(request.getPassword(), userDb.getPassword()));
        });
    }

}