package com.truefalse.services

import com.truefalse.model.*
import com.truefalse.repository.DomainsRepository
import com.truefalse.repository.ReportsRepository
import com.truefalse.repository.UserReportsRepository
import com.truefalse.repository.UsersRepository
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 *
 * @author Filip Lechowicz
 */
@Service
@CompileStatic
@Slf4j
class ReportsService {

    private final ReportsRepository reportsRepository
    private final UsersRepository usersRepository
    private final DomainsRepository domainsRepository
    private final UserReportsRepository userReportsRepository

    @Autowired
    ReportsService(ReportsRepository reportsRepository, UsersRepository usersRepository, DomainsRepository domainsRepository,
                   UserReportsRepository userReportsRepository) {
        this.reportsRepository = reportsRepository
        this.usersRepository = usersRepository
        this.domainsRepository = domainsRepository
        this.userReportsRepository = userReportsRepository
    }


    Map reportArticle(Report report) {
        def reportedArticle = reportsRepository.findByArticleLink(report.articleLink)
        def reportingUser = usersRepository.findByUUID(report.userUUID)
        if (multipleReport(reportedArticle, reportingUser)) {
            return [result: ReportAnswer.CANNOT_REPORTED_MANY_TIMES.name(),
                    score : 0]
        }

        reportingUser.points += 1
        usersRepository.updatePoints(reportingUser)

        def domain = getDomain(report.articleLink)
        domain.domainScore += 1
        domainsRepository.updateScore(domain)

        if (reportedArticle) {
            def userWichReportThisArticle = usersRepository.findByUUID(reportedArticle.userUUID)
            reportingUser.points += 1
            usersRepository.updatePoints(userWichReportThisArticle)
            reportedArticle.score += 1
            reportsRepository.updateScore(reportedArticle)
            userReportsRepository.insert(reportedArticle.reportUUID, reportingUser.uuid)
            [result: ReportAnswer.ALREADY_REPORTED.name(),
             score : reportedArticle.score]
        } else {
            report.domainUUID = domain.domainUUID
            report.score = 1
            def reportUUID = reportsRepository.insert(report)
            userReportsRepository.insert(reportUUID, reportingUser.uuid)
            [result: ReportAnswer.SUCCESSFUL_REPORTED.name(),
             score : report.score]
        }

    }

    Integer reportCount(String link) {
        def article = reportsRepository.findByArticleLink(link)
        article ? article.score : 0
    }

    private Domain getDomain(String link) {
        def uri = new URI(link)
        def domainURL = uri.host
        def domain = domainsRepository.findDomainByDomainLink(domainURL)
        if (!domain) {
            domain = new Domain(domainLink: domainURL, domainScore: 0, status: DomainStatus.NOPE)
            domain.domainUUID = domainsRepository.insert(domain)
        }

        domain
    }

    private Boolean multipleReport(Report report, User user) {
        if (!report || !user) {
            return false
        } else if (!user.uuid || !report.reportUUID) {
            return false
        }
        userReportsRepository.findByUserUUIDAndReportUUID(report.reportUUID, user.uuid).size() != 0
    }
}
