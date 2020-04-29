package com.truefalse.model

import groovy.transform.CompileStatic

/**
 *
 * @author Filip Lechowicz
 */
@CompileStatic
class Report {

    String reportUUID

    String userUUID

    String articleLink

    Integer score

    Date reportDate

    String domainUUID

    String status

    String reason
}
