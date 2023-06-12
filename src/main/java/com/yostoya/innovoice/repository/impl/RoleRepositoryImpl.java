package com.yostoya.innovoice.repository.impl;

import com.yostoya.innovoice.domain.Role;
import com.yostoya.innovoice.exception.ApiException;
import com.yostoya.innovoice.repository.RoleRepository;
import com.yostoya.innovoice.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.yostoya.innovoice.enums.RoleType.ROLE_USER;
import static com.yostoya.innovoice.query.RoleQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {

        log.info("Adding role {} to user id: {}", roleName, userId);

        try {
            var role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY,
                    Map.of("roleName", roleName), new RoleRowMapper());

            jdbc.update(INSERT_ROLE_TO_USER,
                    Map.of("userId", userId, "roleId", requireNonNull(role).getId()));

        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("Something went wrong. Please try again.");
        }

    }

    @Override
    public Role getRoleByUserId(Long userId) {

        log.info("Fetching Role by User id: {}", userId);

        try {

            return jdbc.queryForObject(SELECT_ROLE_BY_USER_ID,
                    Map.of("userId", userId),
                    new RoleRowMapper());

        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException("No role found by User id: " + userId);
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("Something went wrong. Please try again.");
        }
    }

    @Override
    public Role getRoleByUserEmail(String userEmail) {
        return null;
    }

    @Override
    public void updateUserRole(Long roleId, String roleName) {

    }
}
