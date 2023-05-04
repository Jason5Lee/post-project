package me.jason5lee.post_kt_vertx_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.api.*
import me.jason5lee.post_ktor_mongo_fdm.common.utils.InvalidException
import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordEncryptor
import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordVerifier
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult.Invalid
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult.Valid
import java.net.MalformedURLException

// Public constructor for newtypes without validation for ease of construction from database data.

data class Time(val utc: Long)

sealed class Identity {
    data class User(val id: me.jason5lee.post_kt_vertx_fdm.common.UserId) : me.jason5lee.post_kt_vertx_fdm.common.Identity()
    data class Admin(val id: me.jason5lee.post_kt_vertx_fdm.common.AdminId) : me.jason5lee.post_kt_vertx_fdm.common.Identity()
}

data class UserId(val value: String)
data class AdminId(val value: String)
data class UserName(val value: String)

data class User(
    val id: me.jason5lee.post_kt_vertx_fdm.common.UserId,
    val name: me.jason5lee.post_kt_vertx_fdm.common.UserName,
    val creation: me.jason5lee.post_kt_vertx_fdm.common.Time,
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
    data class Text(val value: me.jason5lee.post_kt_vertx_fdm.common.TextPostContent) : me.jason5lee.post_kt_vertx_fdm.common.PostContent()
    data class Url(val value: me.jason5lee.post_kt_vertx_fdm.common.UrlPostContent) : me.jason5lee.post_kt_vertx_fdm.common.PostContent()
}

data class Size(val value: Int)

fun newTime(utc: Long): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.Time> = when {
    utc >= 0 -> Valid(me.jason5lee.post_kt_vertx_fdm.common.Time(utc))
    else -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTime.invalid)
}

private fun isLegalUserNameCharacter(ch: Char): Boolean {
    return ch.isLetterOrDigit() || ch == '_' || ch == '-'
}

fun newUserName(value: String): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.UserName> = when {
    value.isEmpty() -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUserName.empty)
    value.length < 3 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUserName.tooShort)
    value.length > 20 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUserName.tooLong)
    value.any { !me.jason5lee.post_kt_vertx_fdm.common.isLegalUserNameCharacter(it) } -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUserName.containsIllegalCharacter)
    else -> Valid(me.jason5lee.post_kt_vertx_fdm.common.UserName(value))
}

fun newPassword(plain: String): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.Password> =
    when {
        plain.isEmpty() -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidPassword.empty)
        plain.length < 5 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidPassword.tooShort)
        plain.length > 72 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidPassword.tooLong)
        else -> Valid(me.jason5lee.post_kt_vertx_fdm.common.Password(plain))
    }

fun newTitle(value: String, invalidException: InvalidException): me.jason5lee.post_kt_vertx_fdm.common.Title = when {
    value.isEmpty() -> throw invalidException(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTitle.empty)
    value.length < 3 -> throw invalidException(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTitle.tooShort)
    value.length > 20 -> throw invalidException(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTitle.tooLong)
    else -> me.jason5lee.post_kt_vertx_fdm.common.Title(value)
}

fun newTextPostContent(value: String): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.TextPostContent> =
    when {
        value.isEmpty() -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTextPostContent.empty)
        value.length > 65535 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidTextPostContent.tooLong)
        else -> Valid(me.jason5lee.post_kt_vertx_fdm.common.TextPostContent(value))
    }

fun newUrlPostContent(value: String): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.UrlPostContent> =
    when {
        value.isEmpty() -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUrlPostContent.empty)
        value.length > 65535 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUrlPostContent.tooLong)
        else -> try {
            java.net.URL(value)
            Valid(me.jason5lee.post_kt_vertx_fdm.common.UrlPostContent(value))
        } catch (e: MalformedURLException) {
            Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidUrlPostContent.invalid(e.message))
        }
    }

private const val DEFAULT_SIZE = 20
private const val MAX_SIZE = 500

fun newSize(value: Int?): ValidationResult<me.jason5lee.post_kt_vertx_fdm.common.Size> = when {
    value == null -> Valid(me.jason5lee.post_kt_vertx_fdm.common.Size(me.jason5lee.post_kt_vertx_fdm.common.DEFAULT_SIZE))
    value < 0 -> Invalid(me.jason5lee.post_kt_vertx_fdm.common.api.InvalidSize.nonPositiveInteger)
    value > me.jason5lee.post_kt_vertx_fdm.common.MAX_SIZE -> Valid(me.jason5lee.post_kt_vertx_fdm.common.Size(me.jason5lee.post_kt_vertx_fdm.common.MAX_SIZE))
    else -> Valid(me.jason5lee.post_kt_vertx_fdm.common.Size(value))
}
