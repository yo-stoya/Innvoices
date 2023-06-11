package com.yostoya.innovoice.rowmapper;

import com.yostoya.innovoice.domain.Role;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleRowMapper implements RowMapper<Role> {
    @Override
    public Role mapRow(ResultSet result, int rowNum) throws SQLException {
        return Role.builder()
                .id(result.getLong("id"))
                .name(result.getString("name"))
                .permission(result.getString("permission"))
                .build();
    }
}
