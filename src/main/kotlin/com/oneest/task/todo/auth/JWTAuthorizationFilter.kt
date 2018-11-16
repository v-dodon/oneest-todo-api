package com.oneest.task.todo.auth

import com.oneest.task.todo.utils.AUTH_HEADER
import com.oneest.task.todo.utils.SECRET
import com.oneest.task.todo.utils.TOKEN_PREFIX
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import org.apache.commons.lang3.StringUtils
import org.apache.logging.log4j.LogManager
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JWTAuthorizationFilter(authManager: AuthenticationManager, val userDetailsService: UserDetailsService) : BasicAuthenticationFilter(authManager) {

    val LOGGER = LogManager.getLogger(JWTAuthorizationFilter::class)

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, chain: FilterChain) {
        val header = request.getHeader(AUTH_HEADER)

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response)
            return
        }

        try {
            val authentication = getAuthentication(request)
            SecurityContextHolder.getContext().authentication = authentication
        } catch (ex: ExpiredJwtException) {
            LOGGER.error(ex.message, ex)
        }

        chain.doFilter(request, response)
    }

    private fun getAuthentication(request: HttpServletRequest): Authentication? {
        val token = request.getHeader(AUTH_HEADER)

        if (StringUtils.isNotEmpty(token)) {
            val userName = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .body.subject
            val userFromDb =
                    try {
                        userDetailsService.loadUserByUsername(userName)
                    }catch (ex: UsernameNotFoundException) {
                        LOGGER.error(ex.message, ex)
                        null;
                    }

            return if (userName != null && userFromDb != null) UsernamePasswordAuthenticationToken(userName, null, emptyList())
            else null
        }
        return null
    }
}