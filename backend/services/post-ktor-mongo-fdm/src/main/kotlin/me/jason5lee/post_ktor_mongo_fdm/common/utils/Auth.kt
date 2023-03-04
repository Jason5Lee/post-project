package me.jason5lee.post_ktor_mongo_fdm.common.utils

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import me.jason5lee.post_ktor_mongo_fdm.common.AdminId
import me.jason5lee.post_ktor_mongo_fdm.common.Identity
import me.jason5lee.post_ktor_mongo_fdm.common.Time
import me.jason5lee.post_ktor_mongo_fdm.common.UserId
import me.jason5lee.post_ktor_mongo_fdm.common.api.invalidAuth
import java.util.*

class Auth(
    private val jwtVerifier: JWTVerifier,
    private val algorithm: Algorithm,
    private val validSecs: Int,
) {
    fun getTokenExpireTime(): Time = timeFromNow(offset = validSecs.toLong() * 1000)

    fun generateToken(identity: Identity, expire: Time): String {
        val jwtBuilder = JWT.create()
        when (identity) {
            is Identity.Admin -> jwtBuilder.withClaim("adminId", identity.id.value)
            is Identity.User -> jwtBuilder.withClaim("userId", identity.id.value)
        }
        jwtBuilder.withExpiresAt(Date(expire.utc))
        return jwtBuilder.sign(algorithm)
    }

    fun getIdentity(token: String): Identity {
        val payload = try {
            jwtVerifier.verify(token)
        } catch (e: JWTVerificationException) {
            throw invalidAuth()
        }
        val userIdClaim = payload.getClaim("userId")
        val adminIdClaim = payload.getClaim("adminId")

        return if (!userIdClaim.isMissing) {
            if (!adminIdClaim.isMissing) {
                throw invalidAuth()
            }
            Identity.User(UserId(userIdClaim.asString()))
        } else {
            if (adminIdClaim.isMissing) {
                throw invalidAuth()
            }
            Identity.Admin(AdminId(adminIdClaim.asString()))
        }
    }
}
