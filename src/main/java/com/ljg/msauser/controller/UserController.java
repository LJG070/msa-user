package com.ljg.msauser.controller;

import com.ljg.msauser.dto.RequestUser;
import com.ljg.msauser.dto.ResponseUser;
import com.ljg.msauser.dto.UserDto;
import com.ljg.msauser.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.GeneratedValue;
import javax.ws.rs.Path;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
public class UserController {
    private final Environment environment;
    private final UserService userService;

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's User Service"
        + ", port(local.server.port)=" + environment.getProperty("local.server.port")
        + ", port(server.port)=" + environment.getProperty("server.port")
        + ", token secret=" + environment.getProperty("token.secret")
        + ", token expiration=" + environment.getProperty("token.expiration_time"));

    }

    @PostMapping("/users")
    public ResponseEntity createUser(@RequestBody RequestUser user) {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserDto userDto = modelMapper.map(user, UserDto.class);
        userService.createUser(userDto);

        ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(responseUser);
    }

    @GetMapping("/users")
    public ResponseEntity getUsers() {
        Iterable<UserDto> users = this.userService.getUserByAll();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity getUser(@PathVariable("userId") String userId) {
        UserDto user = this.userService.getUserByUserId(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(user);
    }

    @PostMapping("/login")
    public ResponseEntity login() {

        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }
}
