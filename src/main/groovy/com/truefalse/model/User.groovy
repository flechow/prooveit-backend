package com.truefalse.model

import groovy.transform.CompileStatic

/**
 *
 * @author Filip Lechowicz
 */
@CompileStatic
class User {

    String uuid

    String name

    String password

    Integer points

    UserRank rank

}
