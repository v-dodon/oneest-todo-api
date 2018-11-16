package com.oneest.task.todo.auth

import com.fasterxml.jackson.databind.ObjectMapper
import com.oneest.task.todo.model.TodoUser
import com.oneest.task.todo.utils.AUTH_HEADER
import com.oneest.task.todo.utils.SECRET
import com.oneest.task.todo.utils.TOKEN_EXPIRATION_TIME
import com.oneest.task.todo.utils.TOKEN_PREFIX
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.util.*
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthenticationFilter(authManager: AuthenticationManager): UsernamePasswordAuthenticationFilter() {
    init {
        authenticationManager = authManager
        setFilterProcessesUrl("/auth/login")
    }

    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        val credentials = ObjectMapper().readValue(request.inputStream, TodoUser::class.java)
        return authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(
                        credentials.username,
                        credentials.password,
                        emptyList<GrantedAuthority>()))
    }

    override fun successfulAuthentication(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain, authResult: Authentication) {
        val JWT = Jwts.builder()
                .setSubject((authResult.principal as User).username)
                .setExpiration(Date(System.currentTimeMillis() + TOKEN_EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact()
        response.addHeader(AUTH_HEADER, TOKEN_PREFIX + JWT)
    }
}