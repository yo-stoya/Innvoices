package com.yostoya.innovoice.dtomapper;

import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.UserDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = BaseMapper.class)
public interface UserMapper {

    @Mapping(target = "role", source = "id", qualifiedByName = "findRole")
    @Mapping(target = "permissions", source = "id", qualifiedByName = "findPermissions")
    UserDTO toDTO(User user);


    User toUser(UserDTO dto);
}
