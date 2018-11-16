package com.oneest.task.todo.service

import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo

interface TodoService {
    fun addTodo(todo: Todo): Todo
    fun findAllTodos(title: String? = null, text:String? = null, tag:String? = null): List<Todo>
    fun removeTodo(id: Long): Todo?
}