package com.truefalse.services

import com.truefalse.model.User
import com.truefalse.model.UserRank
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
class UserService {

    private final UsersRepository usersRepository

    @Autowired
    UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository
    }

    void addUser(User user) {
        user.points = 0
        user.rank = UserRank.NOOB
        usersRepository.insert(user)
    }

    List<User> getUsers() {
        def users = usersRepository.findAll()
        users.collect {
            new User(points: it.points, name: it.name)
        }

    }

    String login(User user) {
        def userFormDb = usersRepository.findByName(user.name)
        if (!userFormDb) {
            return null
        }
        userFormDb.password == user.password ? userFormDb.uuid : null
    }

}
