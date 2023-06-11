package com.yostoya.innovoice.repository;

import com.yostoya.innovoice.domain.Role;

import java.util.Collection;

public interface RoleRepository<T extends Role> {

    T create(T data);

    Collection<T> list(int page, int pageSize);

    T get(Long id);

    T update(T data);

    Boolean delete(Long id);

    void addRoleToUser(Long userId, String roleName);

    Role getRoleByUserId(Long userId);

    Role getRoleByUserEmail(String userEmail);

    void updateUserRole(Long roleId, String roleName);

}
