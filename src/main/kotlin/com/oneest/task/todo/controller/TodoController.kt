package com.oneest.task.todo.controller

import com.oneest.task.todo.exceptions.NotFoundTodoResponse
import com.oneest.task.todo.model.Todo
import com.oneest.task.todo.service.TodoService
import com.oneest.task.todo.utils.NO_TODO_FOUND_MESSAGE_WITH_ID
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/todo")
class TodoController(val service: TodoService) {

    @PostMapping
    fun addTodo(@RequestBody todo: Todo): ResponseEntity<Long> {
        val savedTodo = service.addTodo(todo)
        return ResponseEntity(savedTodo.id, HttpStatus.CREATED)
    }

    @DeleteMapping("/{id}")
    fun deleteTodo(@PathVariable id: Long): ResponseEntity<*> {
        val removedTodo = service.removeTodo(id)
        val responseEntity = if (removedTodo == null) {
            val noTodoFoundResponse = NotFoundTodoResponse(NO_TODO_FOUND_MESSAGE_WITH_ID + id)
            ResponseEntity(noTodoFoundResponse, HttpStatus.NOT_FOUND)
        } else {
            ResponseEntity(HttpStatus.OK)
        }

        return responseEntity
    }

    @GetMapping
    fun getTodo(@RequestParam(required = false) title: String?, @RequestParam(required = false) text: String?, @RequestParam(required = false) tag: String?): ResponseEntity<List<Todo>> {
        val todos = service.findAllTodos(title, text, tag)

        return ResponseEntity(todos, HttpStatus.OK)
    }

}