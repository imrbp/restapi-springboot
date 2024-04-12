package springbootpnz.restapi.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.LoginUserRequest;
import springbootpnz.restapi.model.TokenResponse;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ValidationService validationService;

    public TokenResponse login(LoginUserRequest loginUserRequest) {
        validationService.Validate(loginUserRequest);

        User user = userRepository.findById(loginUserRequest.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password"));
        if (BCrypt.checkpw(loginUserRequest.getPassword(), user.getPassword())) {
            user.setToken(UUID.randomUUID().toString());
            user.setTokenExpiredAt(next10Days());
            userRepository.save(user);

            return  TokenResponse
                    .builder()
                    .token(user.getToken())
                    .tokenExpiresIn(user.getTokenExpiredAt())
                    .build();
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid username or password");
        }
    };

    @Transactional
    public void logout(User user){
        user.setToken(null);
        user.setTokenExpiredAt(null);
        userRepository.save(user);
    }

    private Long next10Days() {
        return System.currentTimeMillis() + 10 * 24 * 60 * 60 * 1000;
    }
}
