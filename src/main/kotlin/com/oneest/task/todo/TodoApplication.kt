package com.oneest.task.todo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
open class TodoApplication

    fun main(args: Array<String>) {
        SpringApplication.run(TodoApplication::class.java, *args)
    }