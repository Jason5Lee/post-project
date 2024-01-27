package me.jason5lee.post_ktor_mongo_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordVerifier
import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PasswordTests {
    @Test
    fun `should fail if it is empty`() {
        assertNull(newPassword(""))
    }

    @Test
    fun `should fail if it is too short`() {
        val short = "a"
        assertNull(newPassword(short))
    }

    @Test
    fun `should fail if it is too long`() {
        val long = "a".repeat(73)
        assertNull(newPassword(long))
    }

    @Test
    fun `should create a password if it is proper`() {
        val proper = "as#@F02"
        val verifier = object : PasswordVerifier {
            override fun verify(plain: String): Boolean = plain == proper
        }
        assertTrue(newPassword(proper)!!.verify(verifier))
    }
}
