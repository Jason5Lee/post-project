package me.jason5lee.post_ktor_mongo_fdm.common

import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordVerifier
import me.jason5lee.post_ktor_mongo_fdm.common.api.*
import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidTime
import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidTitle
import me.jason5lee.post_ktor_mongo_fdm.common.api.InvalidUserName
import me.jason5lee.post_ktor_mongo_fdm.common.utils.InvalidException
import me.jason5lee.post_ktor_mongo_fdm.common.utils.PasswordEncryptor
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult.Invalid
import me.jason5lee.post_ktor_mongo_fdm.common.utils.ValidationResult.Valid
import java.net.MalformedURLException

@JvmInline value class Time private constructor(val utc: Long) {
    companion object {
        fun validate(utc: Long): ValidationResult<Time> = when {
            utc >= 0 -> Valid(Time(utc))
            else -> Invalid(InvalidTime.invalid, utc)
        }
    }
}

sealed class Identity {
    data class User(val id: UserId) : Identity()
    data class Admin(val id: AdminId) : Identity()
}

@JvmInline value class UserId(val value: String)
@JvmInline value class AdminId(val value: String)
@JvmInline
value class UserName private constructor(val value: String) {
    companion object {
        private fun isLegalUserNameCharacter(ch: Char): Boolean {
            return ch.isLetterOrDigit() || ch == '_' || ch == '-'
        }

        fun validate(value: String): ValidationResult<UserName> = when {
            value.isEmpty() -> Invalid(InvalidUserName.empty, value)
            value.length < 3 -> Invalid(InvalidUserName.tooShort, value)
            value.length > 20 -> Invalid(InvalidUserName.tooLong, value)
            value.any { !isLegalUserNameCharacter(it) } -> Invalid(InvalidUserName.containsIllegalCharacter, value)
            else -> Valid(UserName(value))
        }
    }
}

data class User(
    val id: UserId,
    val name: UserName,
    val creation: Time,
)
@JvmInline
value class Password private constructor(private val plain: String) {
    companion object {
        private val hidden = "<hidden>"
        
        fun validate(plain: String): ValidationResult<Password> =
            when {
                plain.isEmpty() -> Invalid(InvalidPassword.empty, hidden)
                plain.length < 5 -> Invalid(InvalidPassword.tooShort, hidden)
                plain.length > 72 -> Invalid(InvalidPassword.tooLong, hidden)
                else -> Valid(Password(plain))
            }
    }
    fun verify(verifier: PasswordVerifier): Boolean = verifier.verify(plain)
    fun encrypt(encryptor: PasswordEncryptor): String = encryptor.encrypt(plain)

    override fun toString(): String = "Password { plain: \"***\" }"
}
@JvmInline
value class PostId(val value: String)
@JvmInline
value class Title(val value: String) {
    companion object {
        fun validate(value: String, invalidException: InvalidException): Title = when {
            value.isEmpty() -> throw invalidException(InvalidTitle.empty, value)
            value.length < 3 -> throw invalidException(InvalidTitle.tooShort, value)
            value.length > 20 -> throw invalidException(InvalidTitle.tooLong, value)
            else -> Title(value)
        }
    }
}

@JvmInline
value class TextPostContent private constructor(val value: String) {
    companion object {
        fun validate(value: String): ValidationResult<TextPostContent> =
            when {
                value.isEmpty() -> Invalid(InvalidTextPostContent.empty, value)
                value.length > 65535 -> Invalid(InvalidTextPostContent.tooLong, value)
                else -> Valid(TextPostContent(value))
            }
    }
}
@JvmInline
value class UrlPostContent private constructor(val value: String) {
    companion object {
        fun validate(value: String): ValidationResult<UrlPostContent> =
            when {
                value.isEmpty() -> Invalid(InvalidUrlPostContent.empty, value)
                value.length > 65535 -> Invalid(InvalidUrlPostContent.tooLong, value)
                else -> try {
                    java.net.URL(value)
                    Valid(UrlPostContent(value))
                } catch (e: MalformedURLException) {
                    Invalid(InvalidUrlPostContent.invalid(e.message), value)
                }
            }
    }
}

sealed class PostContent {
    data class Text(val value: TextPostContent) : PostContent()
    data class Url(val value: UrlPostContent) : PostContent()
}
@JvmInline
value class Size(val value: Int) {
    companion object {
        private const val DEFAULT_SIZE = 20
        private const val MAX_SIZE = 500

        fun from(value: Int?): ValidationResult<Size> = when {
            value == null -> Valid(Size(DEFAULT_SIZE))
            value < 0 -> Invalid(InvalidSize.nonPositiveInteger, value)
            value > MAX_SIZE -> Valid(Size(MAX_SIZE))
            else -> Valid(Size(value))
        }
    }
}
