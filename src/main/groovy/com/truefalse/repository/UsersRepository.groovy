package com.truefalse.repository

import com.truefalse.model.User
import com.truefalse.model.UserRank
import groovy.transform.CompileStatic
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository

import javax.sql.DataSource
import java.sql.ResultSet
import java.sql.SQLException

/**
 *
 * @author Filip Lechowicz
 */
@Repository
@CompileStatic
class UsersRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate
    private final UserMapper userMapper

    @Autowired
    UsersRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource)
        this.userMapper = new UserMapper()
    }


    void insert(User user) {
        def params = [
                USER_UUID: UUID.randomUUID(),
                NAME     : user.name,
                PASSWORD : user.password,
                POINTS   : user.points,
                RANK     : user.rank.name()
        ]
        jdbcTemplate.update(INSERT_USER, params)
    }

    User findByUUID(String uuid) {
        def result = jdbcTemplate.query(SELECT_BY_UUID, [USER_UUID: UUID.fromString(uuid)], userMapper)
        if (result.size() == 0) {
            null
        } else {
            result.first()
        }
    }

    List<User> findAll() {
        jdbcTemplate.query(SELECT_USERS, userMapper)
    }

    User findByName(String name) {
        jdbcTemplate.queryForObject(SELECT_BY_NAME, [NAME: name], userMapper)
    }

    void updatePoints(User user) {
        jdbcTemplate.update(UPDATE_POINTS_BY_UUID, [USER_UUID: UUID.fromString(user.uuid), POINTS: user.points])
    }

    private final String INSERT_USER = """INSERT INTO NEWS.USERS (USER_UUID, NAME, PASSWORD, POINTS, RANK) 
                VALUES (:USER_UUID, :NAME, :PASSWORD, :POINTS, :RANK)"""

    private final String SELECT_BY_UUID = """SELECT USER_UUID, NAME, PASSWORD, POINTS, RANK FROM NEWS.USERS WHERE USER_UUID=:USER_UUID"""

    private final String SELECT_BY_NAME = """SELECT USER_UUID, NAME, PASSWORD, POINTS, RANK FROM NEWS.USERS WHERE NAME=:NAME"""

    private final String SELECT_USERS = """SELECT USER_UUID, NAME, PASSWORD, POINTS, RANK FROM NEWS.USERS"""

    private final String UPDATE_POINTS_BY_UUID = """UPDATE NEWS.USERS SET POINTS =:POINTS WHERE USER_UUID = :USER_UUID"""

    private class UserMapper implements RowMapper<User> {
        @Override
        User mapRow(ResultSet rs, int rowNum) throws SQLException {
            new User(
                    uuid: rs.getString("USER_UUID"),
                    name: rs.getString("NAME"),
                    password: rs.getString("PASSWORD"),
                    points: rs.getInt("POINTS"),
                    rank: rs.getString("RANK") as UserRank
            )
        }
    }


}

