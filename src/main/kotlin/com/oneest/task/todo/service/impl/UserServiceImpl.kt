package com.oneest.task.todo.service.impl

import com.oneest.task.todo.dao.UserRepository
import com.oneest.task.todo.model.TodoUser
import com.oneest.task.todo.service.UserService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
open class UserServiceImpl(val userRepository: UserRepository, val bCryptPasswordEncoder: BCryptPasswordEncoder): UserService {

    override fun saveUser(user: TodoUser): TodoUser {
        user.password = bCryptPasswordEncoder.encode(user.password)
        val savedUser = userRepository.save(user)

        return savedUser
    }
}