package me.jason5lee.post_ktor_mongo_fdm.common

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

fun newTime(utc: Long): ValidationResult<Time> = when {
    utc >= 0 -> Valid(Time(utc))
    else -> Invalid(InvalidTime.invalid)
}

private fun isLegalUserNameCharacter(ch: Char): Boolean {
    return ch.isLetterOrDigit() || ch == '_' || ch == '-'
}

fun newUserName(value: String): ValidationResult<UserName> = when {
    value.isEmpty() -> Invalid(InvalidUserName.empty)
    value.length < 3 -> Invalid(InvalidUserName.tooShort)
    value.length > 20 -> Invalid(InvalidUserName.tooLong)
    value.any { !isLegalUserNameCharacter(it) } -> Invalid(InvalidUserName.containsIllegalCharacter)
    else -> Valid(UserName(value))
}

fun newPassword(plain: String): ValidationResult<Password> =
    when {
        plain.isEmpty() -> Invalid(InvalidPassword.empty)
        plain.length < 5 -> Invalid(InvalidPassword.tooShort)
        plain.length > 72 -> Invalid(InvalidPassword.tooLong)
        else -> Valid(Password(plain))
    }

fun newTitle(value: String, invalidException: InvalidException): Title = when {
    value.isEmpty() -> throw invalidException(InvalidTitle.empty)
    value.length < 3 -> throw invalidException(InvalidTitle.tooShort)
    value.length > 20 -> throw invalidException(InvalidTitle.tooLong)
    else -> Title(value)
}

fun newTextPostContent(value: String): ValidationResult<TextPostContent> =
    when {
        value.isEmpty() -> Invalid(InvalidTextPostContent.empty)
        value.length > 65535 -> Invalid(InvalidTextPostContent.tooLong)
        else -> Valid(TextPostContent(value))
    }

fun newUrlPostContent(value: String): ValidationResult<UrlPostContent> =
    when {
        value.isEmpty() -> Invalid(InvalidUrlPostContent.empty)
        value.length > 65535 -> Invalid(InvalidUrlPostContent.tooLong)
        else -> try {
            java.net.URL(value)
            Valid(UrlPostContent(value))
        } catch (e: MalformedURLException) {
            Invalid(InvalidUrlPostContent.invalid(e.message))
        }
    }

fun newPage(value: Int): ValidationResult<Page> = when {
    value <= 0 -> Invalid(InvalidPage.invalidPage)
    else -> Valid(Page(value))
}

fun newPageSize(value: Int, maximumPageSize: PageSize): ValidationResult<PageSize> = when {
    value <= 0 -> Invalid(InvalidPageSize.invalidPageSize)
    value > maximumPageSize.value -> Invalid(InvalidPageSize.pageSizeTooLarge)
    else -> Valid(PageSize(value))
}
