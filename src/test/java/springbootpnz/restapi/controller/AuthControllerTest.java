package springbootpnz.restapi.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.LoginUserRequest;
import springbootpnz.restapi.model.TokenResponse;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void testLoginNotFound() throws Exception {
        LoginUserRequest request= new LoginUserRequest();
        request.setUsername("testFailed");
        request.setPassword("password");
        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertNotNull(response.getErrors());
        });
    }



    @Test
    void testLoginWrongPassword() throws Exception {
        LoginUserRequest request= new LoginUserRequest();
        request.setUsername("testFailed");
        request.setPassword("12345678");
        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertNotNull(response.getErrors());
        });
    }



    @Test
    void testLoginSuccess() throws Exception {
        LoginUserRequest request= new LoginUserRequest();
        request.setUsername("testUser");
        request.setPassword("password");
        mockMvc.perform(
                post("/api/users/login")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<TokenResponse> response =  objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<TokenResponse>>() {});

            assertNull( response.getErrors());
            assertNotNull( response.getData().getToken());
            assertNotNull( response.getData().getTokenExpiresIn());


            User userR = userRepository.findById(request.getUsername()).orElse(null);

            assertNotNull(userR);
            assertEquals(response.getData().getToken(), userR.getToken());
            assertEquals(response.getData().getTokenExpiresIn(), userR.getTokenExpiredAt());

        });
    }

    @Test
    void testLogoutFailed() throws Exception{
        mockMvc.perform(
                delete("/api/users/logout")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        ).andExpectAll(
                status().isUnauthorized()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});
            assertNotNull(response.getErrors());
        });
    }

    @Test
    void testLogoutSuccess() throws Exception {
        User user = new User();
        user.setName("JhonDoe");
        user.setUsername("testUser");
        user.setPassword(BCrypt.hashpw("testPassword", BCrypt.gensalt()));
        user.setToken("testToken");
        user.setTokenExpiredAt(System.currentTimeMillis() + 10000000L);

        userRepository.save(user);

        mockMvc.perform(
                delete("/api/users/logout")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                        .header("X-API-TOKEN", user.getToken())
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<WebResponse<String>>() {});

            assertNull(response.getErrors());
            assertEquals(response.getData(), "OK");

            User userR = userRepository.findById(user.getUsername()).orElse(null);
            assertNotNull(userR);
            assertNull(userR.getToken());
            assertNull(userR.getTokenExpiredAt());

        });
    }

}