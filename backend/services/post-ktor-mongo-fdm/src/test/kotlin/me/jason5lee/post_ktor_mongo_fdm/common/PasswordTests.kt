package me.jason5lee.post_ktor_mongo_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidPassword
import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordVerifier
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PasswordTests {
    @Test
    fun `should fail if it is empty`() {
        assertEquals(Password.validate(""), ValidationResult.Invalid(InvalidPassword.empty, ""))
    }

    @Test
    fun `should fail if it is too short`() {
        val short = "a"
        assertEquals(Password.validate(short), ValidationResult.Invalid(InvalidPassword.tooShort, short))
    }

    @Test
    fun `should fail if it is too long`() {
        val long = "a".repeat(73)
        assertEquals(Password.validate(long), ValidationResult.Invalid(InvalidPassword.tooLong, long))
    }

    @Test
    fun `should create a password if it is proper`() {
        val proper = "as#@F02"
        val verifier = object : PasswordVerifier {
            override fun verify(plain: String): Boolean = plain == proper
        }
        assertTrue(Password.validate(proper).assertValid().verify(verifier))
    }
}
