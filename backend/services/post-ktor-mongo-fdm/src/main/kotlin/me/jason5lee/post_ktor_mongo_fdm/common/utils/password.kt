package me.jason5lee.post_ktor_mongo_fdm.common.utils

interface PasswordVerifier {
    fun verify(plain: String): Boolean
}

interface PasswordEncryptor {
    fun encrypt(plain: String): String
}
