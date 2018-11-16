package com.oneest.task.todo.service

import com.oneest.task.todo.model.TodoUser

interface UserService {
    fun saveUser(user: TodoUser): TodoUser
}