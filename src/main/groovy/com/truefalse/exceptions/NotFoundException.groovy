package com.truefalse.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 *
 * @author Filip Lechowicz
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException extends RuntimeException {

    NotFoundException(String var1) {
        super(var1)
    }
}