package com.yostoya.innovoice.repository.impl;

import com.yostoya.innovoice.domain.Role;
import com.yostoya.innovoice.domain.User;
import com.yostoya.innovoice.dto.UserDTO;
import com.yostoya.innovoice.exception.ApiException;
import com.yostoya.innovoice.repository.RoleRepository;
import com.yostoya.innovoice.repository.UserRepository;
import com.yostoya.innovoice.rowmapper.UserRowMapper;
import com.yostoya.innovoice.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.yostoya.innovoice.enums.RoleType.ROLE_USER;
import static com.yostoya.innovoice.enums.VerificationType.ACCOUNT;
import static com.yostoya.innovoice.query.UserQuery.*;
import static java.time.LocalDateTime.now;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.lang3.RandomStringUtils.*;
import static org.apache.commons.lang3.time.DateFormatUtils.format;
import static org.apache.commons.lang3.time.DateUtils.addDays;

@Repository
@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepository<User>, UserDetailsService {

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
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
            log.info("Verification URL: {} ", verificationUrl);

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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        var user = getUserByEmail(email);

        if (user == null) {
            log.error("User not found");
            throw new UsernameNotFoundException("User not found");
        }

        log.info("User found in database");
        return new UserPrincipal(user, roleRepository.getRoleByUserId(user.getId()));
    }

    @Override
    public User getUserByEmail(String email) {

        try {
            return jdbc.queryForObject(SELECT_USER_BY_EMAIL_QUERY, Map.of("email", email), new UserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException("No User found by email");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public void sendVerificationCode(UserDTO user) {
        var expirationDate = format(addDays(new Date(), 1), DATE_FORMAT);
        var verificationCode = randomAlphanumeric(6);

        try {

            jdbc.update(DELETE_VERIFICATION_CODES_BY_USER_ID_QUERY, Map.of("id", user.id()));
            jdbc.update(INSERT_VERIFICATION_CODE_QUERY, Map.of(
                    "code", verificationCode,
                    "expirationDate", expirationDate,
                    "userId", user.id())
            );

//            sendSMS(user.phone(), String.format("""
//                    From: Innovoice
//                    Hi %s,
//                    To activate your account, please enter the verification code: %s
//                    """, user.firstName(), verificationCode));
        log.info("VERIFICATION CODE: {}", verificationCode);

        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
    }

    @Override
    public User verify2FACode(String email, String code) {
        log.info("Code verification started");
        try {

            var userByCode = jdbc.queryForObject(SELECT_USER_BY_USER_CODE_QUERY,
                    Map.of("code", code), new UserRowMapper());

            var userByEmail = getUserByEmail(email);

            if (userByCode != null && userByCode.getEmail().equals(userByEmail.getEmail())) {
                log.info("Code verified");
                jdbc.update(DELETE_VERIFICATION_CODE_QUERY, Map.of("code", code));
                return userByCode;
            }

            throw new ApiException("Code is invalid. Please try again");

        } catch (EmptyResultDataAccessException ex) {
            throw new ApiException("Record not found");
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw new ApiException("An error occurred. Please try again.");
        }
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
                .path("/user/verify")
                .path("/" + type)
                .path("/" + key)
                .toUriString();
    }

}
