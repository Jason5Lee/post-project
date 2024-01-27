package me.jason5lee.post_ktor_mongo_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordEncryptor
import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordVerifier
import java.net.MalformedURLException

// Public constructor for newtypes without validation for ease of construction from database data.

data class Time(val utc: Long)

sealed class Identity {
    data class User(val id: UserId) : Identity()
    object Admin : Identity()
}

data class UserId(val value: String)
data class UserName(val value: String)

data class User(
    val id: UserId,
    val name: UserName,
    val creation: Time,
)

data class Password(private val plain: String) {
    fun verify(verifier: PasswordVerifier): Boolean = verifier.verify(plain)
    fun encrypt(encryptor: PasswordEncryptor): String = encryptor.encrypt(plain)

    override fun toString(): String = "Password { plain: \"***\" }"
}

data class PostId(val value: String)
data class Title(val value: String)

data class TextPostContent(val value: String)
data class UrlPostContent(val value: String)

sealed class PostContent {
    data class Text(val value: TextPostContent) : PostContent()
    data class Url(val value: UrlPostContent) : PostContent()
}

data class Page(val value: Int)
data class PageSize(val value: Int)

fun newTime(utc: Long): Time? = when {
    utc >= 0 -> Time(utc)
    else -> null
}

private fun isLegalUserNameCharacter(ch: Char): Boolean {
    return ch.isLetterOrDigit() || ch == '_' || ch == '-'
}

fun newUserName(value: String): UserName? =
    if (value.length in 3..20 && value.all { isLegalUserNameCharacter(it) }) {
        UserName(value)
    } else {
        null
    }

fun newPassword(plain: String): Password? =
    if (plain.length in 5..72) { // Limitation of bcrypt
        Password(plain)
    } else {
        null
    }

fun newTitle(value: String): Title? =
    if (value.length in 3..20) {
        Title(value)
    } else {
        null
    }

fun newTextPostContent(value: String): TextPostContent? =
    if (value.length <= 65535) {
        TextPostContent(value)
    } else {
        null
    }

fun newUrlPostContent(value: String): UrlPostContent? =
    if (value.length <= 65535) {
        try {
            java.net.URL(value)
            UrlPostContent(value)
        } catch (e: MalformedURLException) {
            null
        }
    } else {
        null
    }

fun newPage(value: Int): Page? =
    if (value > 0) {
        Page(value)
    } else {
        null
    }

fun newPageSize(value: Int): PageSize? =
    if (value in 1..50) {
        PageSize(value)
    } else {
        null
    }
