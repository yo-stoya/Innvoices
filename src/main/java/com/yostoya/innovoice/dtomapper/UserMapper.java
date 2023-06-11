package com.yostoya.innovoice.dtomapper;

import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    User toUser(UserDTO dto);
}
