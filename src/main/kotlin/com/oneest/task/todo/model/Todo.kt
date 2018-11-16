package com.oneest.task.todo.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.oneest.task.todo.utils.TagDeserializer
import java.util.*
import javax.persistence.*

@Entity
class Todo (
        @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator="todo_seq")
    @SequenceGenerator(name="todo_seq", sequenceName="todo_id_seq", allocationSize=1)
    var  id: Long = 0,
        var title: String? = null,
        var text: String? = null,
        @JsonDeserialize(using = TagDeserializer::class)
    @Enumerated
    var tag: Tag? = null,
        @Temporal(TemporalType.TIMESTAMP)
    var creationDate: Date? = null,
        @Temporal(TemporalType.TIMESTAMP)
    var editDate: Date? = null
)