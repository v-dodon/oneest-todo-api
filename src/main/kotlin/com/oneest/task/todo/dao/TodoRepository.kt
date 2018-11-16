package com.oneest.task.todo.dao

import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.CrudRepository

interface TodoRepository: CrudRepository<Todo, Long>, JpaSpecificationExecutor<Todo> {
    fun findByTitle(title : String): Todo
    fun findByTextContaining(text: String): List<Todo>
    fun findByTag(tag: Tag): List<Todo>

    fun findByTitleAndTagAndTextContaining(title: String, tag: Tag, text: String): Todo
}