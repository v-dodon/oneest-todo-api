package com.oneest.task.todo.model

import javax.persistence.*

@Entity
class TodoUser(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="user_seq")
        @SequenceGenerator(name="user_seq", sequenceName="user_id_seq", allocationSize=1)
        var id: Long = 0,
        var username: String = "",
        var password: String = ""
)