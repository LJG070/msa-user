package com.ljg.msauser.service;

import com.ljg.msauser.dto.ResponseOrder;
import com.ljg.msauser.dto.ResponseUser;
import com.ljg.msauser.dto.UserDto;
import com.ljg.msauser.jpa.UserEntity;
import com.ljg.msauser.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MatchingStrategy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());

        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));

        userRepository.save(userEntity);

        return null;
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId).orElseThrow(EntityNotFoundException::new);
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

        List<ResponseOrder> orders = new ArrayList<>();
        userDto.setOrders(orders);

        return userDto;
    }

    @Override
    public Iterable<UserDto> getUserByAll() {

        List<UserEntity> users = userRepository.findAll();
        List<UserDto> userDtoList = new ArrayList<>();

        for(UserEntity user : users) {
            userDtoList.add(new ModelMapper().map(user, UserDto.class));
        }

        return userDtoList;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        Optional<UserEntity> user = userRepository.findByEmail(email);

        if(!user.isPresent())
            throw new UsernameNotFoundException(email);

        UserDto userDto = new ModelMapper().map(user.get(), UserDto.class);

        System.out.println(userDto);

        return userDto;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email).orElse(null);

        System.out.println(userEntity);

        if(userEntity==null)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
                true, true, true, true,
                new ArrayList<>());
    }
}
