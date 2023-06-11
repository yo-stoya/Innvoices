package com.yostoya.innovoice.rowmapper;

import com.yostoya.innovoice.domain.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet result, int rowNum) throws SQLException {
        return User.builder()
                        .id(result.getLong("id"))
                        .firstName(result.getString("first_name"))
                        .lastName(result.getString("last_name"))
                        .email(result.getString("email"))
                        .password(result.getString("password"))
                        .address(result.getString("address"))
                        .phone(result.getString("phone"))
                        .title(result.getString("title"))
                        .bio(result.getString("bio"))
                        .imageUrl(result.getString("image_url"))
                        .enabled(result.getBoolean("enabled"))
                        .isUsingMfa(result.getBoolean("using_mfa"))
                        .isNotLocked(result.getBoolean("non_locked"))
                        .createdOn(result.getTimestamp("created_on").toLocalDateTime())
                        .build();
    }
}
