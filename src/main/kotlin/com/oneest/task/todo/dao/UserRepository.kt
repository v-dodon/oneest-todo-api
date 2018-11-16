package com.oneest.task.todo.dao

import com.oneest.task.todo.model.TodoUser
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<TodoUser, Long> {
    fun findByUsername(username: String): TodoUser?
}