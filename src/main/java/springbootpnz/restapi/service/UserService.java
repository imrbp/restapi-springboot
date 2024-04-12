package springbootpnz.restapi.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import springbootpnz.restapi.entity.User;
import springbootpnz.restapi.model.RegisterUserRequest;
import springbootpnz.restapi.model.UpdateUserRequest;
import springbootpnz.restapi.model.UserResponse;
import springbootpnz.restapi.repository.UserRepository;
import springbootpnz.restapi.security.BCrypt;

import java.util.Objects;


@Service
public class UserService {
    @Autowired
    public UserRepository userRepository;

    @Autowired
    private ValidationService validationService;


    @Transactional
    public void registerUser(RegisterUserRequest request) {

        validationService.Validate(request);


        if(userRepository.existsById(request.getUsername())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user.setName(request.getName());

        userRepository.save(user);
    }

    public UserResponse get(User user) {
        return UserResponse.builder()
                .username(user.getUsername())
                .name(user.getName())
                .build();
    }

    @Transactional
    public UserResponse update(User user, UpdateUserRequest request) {
        validationService.Validate(request);
        if (Objects.nonNull(request.getName())) {
            user.setName(request.getName());
        }
        if (Objects.nonNull(request.getPassword())) {
            user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        }

        userRepository.save(user);

        return UserResponse.builder()
                .name(user.getName())
                .username(user.getUsername())
                .build();
    };


}
