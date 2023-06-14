package com.yostoya.innovoice.service;

import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.UserDTO;

public interface UserService {
    UserDTO createUser(User user);

    UserDTO getUserByEmail(String email);

    void sendVerificationCode(UserDTO user);

    UserDTO verify2FACode(String email, String code);
}
