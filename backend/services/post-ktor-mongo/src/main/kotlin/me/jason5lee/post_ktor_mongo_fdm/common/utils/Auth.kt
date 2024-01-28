package me.jason5lee.post_ktor_mongo.common.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import me.jason5lee.post_ktor_mongo.common.Identity
import me.jason5lee.post_ktor_mongo.common.Time
import me.jason5lee.post_ktor_mongo.common.UserId
import me.jason5lee.post_ktor_mongo.common.api.Token
import me.jason5lee.post_ktor_mongo.common.api.invalidAuth
import java.util.*

class Auth(
    private val jwtVerifier: JWTVerifier,
    private val algorithm: Algorithm,
    private val validSecs: Int,
    private val adminToken: String,
) {
    fun getTokenExpireTime(): Time = timeFromNow(offset = validSecs.toLong() * 1000)

    fun generateUserToken(user: UserId, expire: Time): String {
        val jwtBuilder = JWT.create()
        jwtBuilder.withClaim("userId", user.value)
        jwtBuilder.withExpiresAt(Date(expire.utc))
        return jwtBuilder.sign(algorithm)
    }

    fun getIdentity(token: Token): Identity = when (token) {
        is Token.Admin -> {
            if (token.value != adminToken) {
                throw invalidAuth()
            }
            Identity.Admin
        }

        is Token.User -> {
            val payload = try {
                jwtVerifier.verify(token.value)
            } catch (e: JWTVerificationException) {
                throw invalidAuth()
            }
            val userIdClaim = payload.getClaim("userId")

            if (userIdClaim.isMissing) {
                throw invalidAuth()
            } else {
                Identity.User(UserId(userIdClaim.asString()))
            }
        }
    }
}
