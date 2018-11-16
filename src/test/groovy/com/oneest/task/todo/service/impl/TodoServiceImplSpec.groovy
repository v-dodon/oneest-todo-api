package com.oneest.task.todo.service.impl

import com.oneest.task.todo.dao.TodoRepository
import com.oneest.task.todo.model.Tag
import com.oneest.task.todo.model.Todo
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification
import spock.lang.Unroll

class TodoServiceImplSpec extends Specification {
    def repository = Mock(TodoRepository)
    def service = new TodoServiceImpl(repository)

    def "should add a todo"() {
        given:
        def todo = new Todo()
        todo.id = 1L
        todo.title = "test"
        todo.text = "testText"
        todo.tag = Tag.OBISNUIT

        repository.save(_) >> todo

        when:
        def savedTodo = service.addTodo(todo)

        then:
        savedTodo != null
        savedTodo.creationDate != null
        savedTodo.editDate != null
    }

    @Unroll
    def "should get #message"(String message, String title, String text, String tag) {
        given:
        repository.findAll() >> Collections.emptyList()
        repository.findAll(_ as org.springframework.data.jpa.domain.Specification) >> Collections.emptyList()

        when:
        service.findAllTodos(title, text, tag)

        then:
        if (StringUtils.isAllEmpty(title, text, tag)) {
            repository.findAll() * 1
        } else {
            repository.findAll(_ as org.springframework.data.jpa.domain.Specification) * 1
        }

        where:
        message                  | title   | text   | tag
        "all todos"              | null    | null   | null
        "all todos with filters" | "title" | "text" | Tag.OBISNUIT
    }

    @Unroll
    def "should remove the todo if there is one and return the removed"(Todo searchedTodo) {
        given:
        repository.findById(_) >> Optional.ofNullable(searchedTodo)

        when:
        def removedTodo = service.removeTodo(1L)

        then:
        removedTodo == searchedTodo

        where:
        searchedTodo                 | _
        new Todo(title: "TitleTest") | _
        null                         | _
    }
}
