package com.ljg.msauser.service;

import com.ljg.msauser.dto.UserDto;
import com.ljg.msauser.jpa.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUserByUserId(String userId);
    Iterable<UserDto>  getUserByAll();
    UserDto getUserDetailsByEmail(String email);
}
