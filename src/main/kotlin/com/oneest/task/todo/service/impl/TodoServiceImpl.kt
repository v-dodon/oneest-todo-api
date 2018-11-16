package com.oneest.task.todo.service.impl

import com.oneest.task.todo.dao.TodoRepository
import com.oneest.task.todo.dao.TodoSpecification
import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo
import com.oneest.task.todo.service.TodoService
import org.apache.commons.lang3.StringUtils
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional
open class TodoServiceImpl(val repository: TodoRepository): TodoService {

    override fun addTodo(todo: Todo): Todo {
        todo.creationDate = Date()
        todo.editDate = Date()

        return repository.save(todo)
    }

    override fun findAllTodos(title: String?, text: String?, tag: String?): List<Todo> {
        if(StringUtils.isAllEmpty(title, text, tag)) {
            return repository.findAll().toList()
        }

        val filter = Todo()
        filter.title = title
        filter.text = text
        filter.tag = if (tag != null) Tag.valueOf(tag.toUpperCase()) else null

        val spec : Specification<Todo> = TodoSpecification(filter)

        return repository.findAll(spec)
    }

    override fun removeTodo(id: Long): Todo? {
        val todoToRemove = repository.findById(id)

        todoToRemove.ifPresent { todo -> repository.delete(todo) }
        return todoToRemove.orElse(null)
    }
}