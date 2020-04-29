package com.truefalse.repository

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
class UserReportsRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate
    private final UserReportMapper reportMapper

    @Autowired
    UserReportsRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource)
        this.reportMapper = new UserReportMapper()
    }

    void insert(String reportUUID, String userUUID) {
        def params = [
                USER_UUID  : UUID.fromString(userUUID),
                REPORT_UUID: UUID.fromString(reportUUID)
        ]

        jdbcTemplate.update(INSERT_USER_REPORT, params)
    }

    List findByUserUUIDAndReportUUID(String reportUUID, String userUUID) {
        def params = [
                USER_UUID  : userUUID ? UUID.fromString(userUUID) : null,
                REPORT_UUID: reportUUID ? UUID.fromString(reportUUID) : null
        ]
        jdbcTemplate.query(SELECT_BY_USER_UUID_AND_REPORT_UUID, params, this.reportMapper)
    }

    private final String INSERT_USER_REPORT = """INSERT INTO NEWS.USER_REPORTS (USER_UUID, REPORT_UUID) VALUES (:USER_UUID, :REPORT_UUID)"""

    private final String SELECT_BY_USER_UUID_AND_REPORT_UUID = """SELECT USER_UUID, REPORT_UUID FROM NEWS.USER_REPORTS WHERE USER_UUID = :USER_UUID AND REPORT_UUID = :REPORT_UUID """

    private class UserReportMapper implements RowMapper<Map> {
        @Override
        Map mapRow(ResultSet rs, int rowNum) throws SQLException {
            [
                    USER_UUID  : rs.getString('USER_UUID'),
                    REPORT_UUID: rs.getString('REPORT_UUID')
            ]
        }
    }
}
