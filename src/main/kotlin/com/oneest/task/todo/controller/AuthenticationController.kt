package com.oneest.task.todo.controller

import com.oneest.task.todo.model.TodoUser
import com.oneest.task.todo.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthenticationController(val userService: UserService) {

    @PostMapping("/register")
    fun register(@RequestBody user: TodoUser): ResponseEntity<Long> {
        val savedUser = userService.saveUser(user)

        return ResponseEntity(savedUser.id, HttpStatus.CREATED)
    }
}