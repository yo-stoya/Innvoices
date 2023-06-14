package com.yostoya.innovoice.dtomapper;


import com.yostoya.innovoice.domain.Role;
import com.yostoya.innovoice.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.MapperConfig;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
@MapperConfig
@RequiredArgsConstructor
public class BaseMapper {

    private final RoleRepository<Role> roleRepository;

    private Role findRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }

    @Named("findRole")
    public String findRole(Long id) {
        return findRoleByUserId(id).getName();
    }

    @Named("findPermissions")
    public String findPermissions(Long id) {
        return findRoleByUserId(id).getPermission();
    }
}
