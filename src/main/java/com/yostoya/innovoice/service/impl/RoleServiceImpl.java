package com.yostoya.innovoice.service.impl;

import com.yostoya.innovoice.domain.Role;
import com.yostoya.innovoice.repository.RoleRepository;
import com.yostoya.innovoice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository<Role> roleRepository;

    @Override
    public Role getRoleByUserId(Long id) {
        return roleRepository.getRoleByUserId(id);
    }
}
