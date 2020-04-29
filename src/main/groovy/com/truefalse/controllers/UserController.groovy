package com.truefalse.controllers

import com.truefalse.exceptions.NotFoundException
import com.truefalse.model.User
import com.truefalse.services.UserService
import groovy.util.logging.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

/**
 *
 * @author Filip Lechowicz
 */
@RestController
@RequestMapping("/users")
@Slf4j
class UserController {

    private final UserService userService


    @Autowired
    UserController(UserService userService) {
        this.userService = userService
    }

    @PostMapping("/add")
    void addNewUsers(@RequestBody User user) {
        userService.addUser(user)
    }

    @PostMapping("/login")
    Map login(@RequestBody User user) {
        def uuid = userService.login(user)
        if (uuid) {
            [userUUID: uuid]
        } else {
            throw new NotFoundException("User not found")
        }
    }

    @GetMapping
    List<User> getUsers() {
        userService.getUsers()
    }

}
