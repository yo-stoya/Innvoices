package com.yostoya.innovoice.query;

public class RoleQuery {
    public static final String SELECT_ROLE_BY_NAME_QUERY = """
            SELECT * FROM Roles WHERE name = :roleName
            """;
    public static final String INSERT_ROLE_TO_USER = """
            INSERT INTO UserRoles (user_id, role_id)
            VALUES (:userId, :roleId)
            """;
}
