package com.yostoya.innovoice.service.impl;

import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.UserDTO;
import com.yostoya.innovoice.dtomapper.UserMapper;
import com.yostoya.innovoice.repository.UserRepository;
import com.yostoya.innovoice.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository<User> userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(User user) {
        return userMapper.toDTO(userRepository.create(user));
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        return userMapper.toDTO(userRepository.getUserByEmail(email));
    }

}
