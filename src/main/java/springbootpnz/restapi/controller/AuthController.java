package springbootpnz.restapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.LoginUserRequest;
import springbootpnz.restapi.model.TokenResponse;
import springbootpnz.restapi.model.WebResponse;
import springbootpnz.restapi.service.AuthService;

@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(
            path = "/api/users/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<TokenResponse> login(@RequestBody LoginUserRequest request) {
        TokenResponse tokenResponse = authService.login(request);
        return WebResponse.<TokenResponse>builder().data(tokenResponse).build();
    }



    @DeleteMapping(
            path = "/api/users/logout",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public WebResponse<String> login(User user) {
        authService.logout(user);
        return WebResponse.<String>builder().data("OK").build();
    };
}
