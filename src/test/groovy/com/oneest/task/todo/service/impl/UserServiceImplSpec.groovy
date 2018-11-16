package com.oneest.task.todo.service.impl

import com.oneest.task.todo.dao.UserRepository
import com.oneest.task.todo.model.TodoUser
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import spock.lang.Specification

class UserServiceImplSpec extends Specification {
    def repository = Mock(UserRepository)
    def bCryptPasswordEncoder = Mock(BCryptPasswordEncoder)
    def service = new UserServiceImpl(repository, bCryptPasswordEncoder)

    def "should add user and encrypt the password"() {
        given:
        def encodedPassword = "encodedPassword"
        def user = new TodoUser()
        user.username = "testUser"
        user.password = "testPassword"
        bCryptPasswordEncoder.encode(user.password) >> encodedPassword
        repository.save(user) >> user

        when:
        def savedUser = service.saveUser(user)

        then:
        savedUser.password == encodedPassword
    }
}
