package com.oneest.task.todo.testUtils

import com.oneest.task.todo.utils.SECRET
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

class TestUtils {
    companion object {
        fun createJWT(expirationDate: Long): String {
            return Jwts.builder()
                    .setSubject("test")
                    .setExpiration(Date(expirationDate))
                    .signWith(SignatureAlgorithm.HS512, SECRET)
                    .compact()
        }
    }
}