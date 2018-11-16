package com.oneest.task.todo.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.oneest.task.todo.exceptions.AuthenticationFailedResponse
import com.oneest.task.todo.utils.AUTH_FAILED_MESSAGE
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class CustomAuthenticationEntryPoint: AuthenticationEntryPoint{
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authEx: AuthenticationException) {
        val objectMapper = ObjectMapper()
        val ex = AuthenticationFailedResponse(AUTH_FAILED_MESSAGE)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.writer.write(objectMapper.writeValueAsString(ex))
    }
}