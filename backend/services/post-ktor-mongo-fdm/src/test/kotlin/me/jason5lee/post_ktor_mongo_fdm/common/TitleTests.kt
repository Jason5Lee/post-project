package me.jason5lee.post_ktor_mongo_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidTitle
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ExpectedException
import me.jason5lee.post_ktor_mongo_fdm.common.utils.onInvalid
import kotlin.test.Test
import kotlin.test.assertEquals

class TitleTests {
    @Test
    fun `should fail if it is empty`() {
        try {
            Title.validate("", onInvalid { body, _ -> ExpectedException(body) })
            throw Exception("Should not be reached")
        } catch (e: ExpectedException) {
            assertEquals(InvalidTitle.empty, e.info)
        }
    }

    @Test
    fun `should fail if it is too short`() {
        try {
            Title.validate("a", onInvalid { body, _ -> ExpectedException(body) })
            throw Exception("Should not be reached")
        } catch (e: ExpectedException) {
            assertEquals(InvalidTitle.tooShort, e.info)
        }
    }

    @Test
    fun `should fail if it is too long`() {
        try {
            Title.validate("a".repeat(21), onInvalid { body, _ -> ExpectedException(body) })
            throw Exception("Should not be reached")
        } catch (e: ExpectedException) {
            assertEquals(InvalidTitle.tooLong, e.info)
        }
    }

    @Test
    fun `should create a title if it is proper`() {
        val proper = "a".repeat(20)
        assertEquals(proper, Title.validate(proper, onInvalid { _, _ -> throw Exception("Should not be reached") }).value)
    }
}
