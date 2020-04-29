package com.truefalse.repository

import com.truefalse.model.Report
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
class ReportsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate
    private final ReportMapper reportMapper

    @Autowired
    ReportsRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource)
        this.reportMapper = new ReportMapper()
    }


    String insert(Report report) {
        def uuid = UUID.randomUUID()
        def params = [
                REPORT_UUID : uuid,
                USER_UUID   : UUID.fromString(report.userUUID),
                ARTICLE_LINK: report.articleLink,
                REPORT_SCORE: report.score,
                DOMAIN_UUID : UUID.fromString(report.domainUUID),
                STATUS      : report.status,
                REASON      : report.reason

        ]
        jdbcTemplate.update(INSERT_REPORT, params)

        uuid.toString()
    }

    Report findByArticleLink(String articleLink) {
        def result = jdbcTemplate.query(SELECT_BY_ARTICLE_LINK, [ARTICLE_LINK: articleLink], reportMapper)
        if (result.size() == 0) {
            null
        } else {
            result.first()
        }
    }

    List<Report> findByUserUUID(String userUUID) {
        jdbcTemplate.query(SELECT_BY_USER_UUID, [USER_UUID: UUID.fromString(userUUID)], reportMapper)
    }

    void updateScore(Report report) {
        def params = [
                REPORT_SCORE: report.score,
                REPORT_UUID : UUID.fromString(report.reportUUID)
        ]
        jdbcTemplate.update(UPDATE_SCORE_BY_REPORT_UUID, params)
    }

    private final String INSERT_REPORT = """INSERT INTO NEWS.REPORTS (REPORT_UUID, USER_UUID, ARTICLE_LINK, REPORT_SCORE,REPORT_DATE, DOMAIN_UUID, STATUS, REASON) VALUES (:REPORT_UUID, :USER_UUID, :ARTICLE_LINK, :REPORT_SCORE, CURRENT_TIMESTAMP , :DOMAIN_UUID, :STATUS, :REASON)"""

    private final String SELECT_BY_ARTICLE_LINK = """SELECT REPORT_UUID, USER_UUID, ARTICLE_LINK, REPORT_SCORE, REPORT_DATE, DOMAIN_UUID, STATUS, REASON
                 FROM NEWS.REPORTS WHERE ARTICLE_LINK=:ARTICLE_LINK"""

    private final String SELECT_BY_USER_UUID = """SELECT REPORT_UUID, USER_UUID, ARTICLE_LINK, REPORT_SCORE, REPORT_DATE, DOMAIN_UUID, STATUS, REASON
                 FROM NEWS.REPORTS WHERE USER_UUID=:USER_UUID"""

    private final String UPDATE_SCORE_BY_REPORT_UUID = """UPDATE NEWS.REPORTS SET REPORT_SCORE = :REPORT_SCORE WHERE REPORT_UUID = :REPORT_UUID"""

    private class ReportMapper implements RowMapper<Report> {
        @Override
        Report mapRow(ResultSet rs, int rowNum) throws SQLException {
            new Report(
                    reportUUID: rs.getString("REPORT_UUID"),
                    userUUID: rs.getString("USER_UUID"),
                    articleLink: rs.getString("ARTICLE_LINK"),
                    score: rs.getInt("REPORT_SCORE"),
                    reportDate: rs.getDate("REPORT_DATE"),
                    domainUUID: rs.getString("DOMAIN_UUID"),
                    status: rs.getString("STATUS"),
                    reason: rs.getString("REASON")

            )
        }
    }
}
