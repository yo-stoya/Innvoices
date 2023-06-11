package com.yostoya.innovoice.repository.impl;

import com.yostoya.innovoice.domain.Role;
import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.exception.ApiException;
import com.yostoya.innovoice.repository.RoleRepository;
import com.yostoya.innovoice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import static com.yostoya.innovoice.enums.RoleType.ROLE_USER;
import static com.yostoya.innovoice.enums.VerificationType.ACCOUNT;
import static com.yostoya.innovoice.query.UserQuery.*;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User> {

    private final NamedParameterJdbcTemplate jdbc;
    private final RoleRepository<Role> roleRepository;
    private final PasswordEncoder encoder;

    @Override
    public User create(User user) {

        if (getEmailCount(user.getEmail().trim().toLowerCase()) > 0) {
            throw new ApiException("Email already in use. Please use other.");
        }

        try {
            var keyHolder = new GeneratedKeyHolder();
            var parameters = getSqlParameters(user);

            jdbc.update(INSERT_USER_QUERY, parameters, keyHolder);

            user.setId(requireNonNull(keyHolder.getKey()).longValue());
            user.setCreatedOn(now());

            roleRepository.addRoleToUser(user.getId(), ROLE_USER.name());
            var verificationUrl = getVerificationUrl(UUID.randomUUID().toString(), ACCOUNT.getType());

            jdbc.update(INSERT_ACCOUNT_VERIFICATION_URL_QUERY,
                    Map.of("userId", user.getId(), "url", verificationUrl));
            // emailService.sendVerificationUrl(user.getFirstName(), user.getEmail(), verificationUrl, ACCOUNT);

            user.setEnabled(false);
            user.setIsNotLocked(true);

            return user;

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("Something went wrong. Please try again.");
        }
    }

    @Override
    public Collection<User> list(int page, int pageSize) {
        return null;
    }

    @Override
    public User get(Long id) {
        return null;
    }

    @Override
    public User update(User data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    private Integer getEmailCount(String email) {
        return jdbc.queryForObject(COUNT_USER_EMAIL_QUERY, Map.of("email", email), Integer.class);
    }

    private SqlParameterSource getSqlParameters(User user) {
        return new MapSqlParameterSource()
                .addValue("firstName", user.getFirstName())
                .addValue("lastName", user.getLastName())
                .addValue("email", user.getEmail())
                .addValue("password", encoder.encode(user.getPassword()));
    }

    private String getVerificationUrl(String key, String type) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/verify")
                .path("/" + type)
                .path("/" + key)
                .toUriString();
    }

}
