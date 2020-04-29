package com.truefalse.controllers

import com.truefalse.model.Report
import com.truefalse.services.ReportsService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Filip Lechowicz
 */
@RestController
@RequestMapping("/reports")
class ReportsController {

    private final ReportsService reportsService

    @Autowired
    ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService
    }

    @PostMapping("/article")
    @Transactional
    Map report(@RequestBody Report report) {
        reportsService.reportArticle(report)
    }

    @GetMapping("/stat")
    Map reportCount(@RequestParam String link) {
        [reportCount: reportsService.reportCount(link)]
    }
}
