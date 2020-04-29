package com.truefalse.repository

import com.truefalse.model.Domain
import com.truefalse.model.DomainStatus
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
class DomainsRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate
    private final DomainMapper domainMapper

    @Autowired
    DomainsRepository(DataSource dataSource) {
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource)
        this.domainMapper = new DomainMapper()
    }

    String insert(Domain domain) {
        def uuid = UUID.randomUUID()
        def params = [
                DOMAIN_UUID        : uuid,
                DOMAIN_LINK        : domain.domainLink,
                DOMAIN_REPORT_SCORE: domain.domainScore,
                STATUS             : domain.status.name()
        ]
        jdbcTemplate.update(INSERT_DOMAIN, params)

        uuid.toString()
    }

    Domain findDomainByDomainLink(String domainLink) {
        def result = jdbcTemplate.query(SELECT_DOMAIN_BY_NAME, [DOMAIN_LINK: domainLink], domainMapper)
        if (result.size() == 0) {
            null
        } else {
            result.first()
        }
    }

    void updateScore(Domain domain) {
        def params = [
                DOMAIN_REPORT_SCORE: domain.domainScore,
                DOMAIN_UUID        : UUID.fromString(domain.domainUUID)
        ]
        jdbcTemplate.update(UPDATE_SCORE_BY_DOMAIN_UUID, params)
    }

    private final String INSERT_DOMAIN = """INSERT INTO NEWS.DOMAINS (DOMAIN_UUID, DOMAIN_LINK, DOMAIN_REPORT_SCORE, STATUS) 
                VALUES (:DOMAIN_UUID, :DOMAIN_LINK, :DOMAIN_REPORT_SCORE, :STATUS)"""

    private final String SELECT_DOMAIN_BY_NAME = """SELECT DOMAIN_UUID, DOMAIN_LINK, DOMAIN_REPORT_SCORE, STATUS FROM NEWS.DOMAINS WHERE DOMAIN_LINK = :DOMAIN_LINK """

    private final String UPDATE_SCORE_BY_DOMAIN_UUID = """UPDATE NEWS.DOMAINS SET DOMAIN_REPORT_SCORE = :DOMAIN_REPORT_SCORE WHERE DOMAIN_UUID = :DOMAIN_UUID"""


    private class DomainMapper implements RowMapper<Domain> {
        @Override
        Domain mapRow(ResultSet rs, int rowNum) throws SQLException {
            new Domain(
                    domainUUID: rs.getString("DOMAIN_UUID"),
                    domainLink: rs.getString("DOMAIN_LINK"),
                    domainScore: rs.getInt("DOMAIN_REPORT_SCORE"),
                    status: rs.getString("STATUS") as DomainStatus
            )
        }
    }
}
