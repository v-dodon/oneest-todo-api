package com.oneest.task.todo.utils

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.oneest.task.todo.model.Tag

/**
 * Custom deserializer from json string to Tag enum element
 */
class TagDeserializer : JsonDeserializer<Tag>() {
    override fun deserialize(jp: JsonParser, p1: DeserializationContext): Tag {
        return Tag.valueOf(jp.valueAsString.toUpperCase())
    }
}