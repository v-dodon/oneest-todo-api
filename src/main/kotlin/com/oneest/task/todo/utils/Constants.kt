package com.oneest.task.todo.utils

val SECRET = "secretKey"
val TOKEN_PREFIX = "Bearer "
val AUTH_HEADER = "Authorization"
val TOKEN_EXPIRATION_TIME: Long = 86_400_000
val AUTH_FAILED_MESSAGE = "You are not logged in or the session has been expired, to use this resource you need to be logged in"
val NO_TODO_FOUND_MESSAGE_WITH_ID = "There are no todo with the id: "