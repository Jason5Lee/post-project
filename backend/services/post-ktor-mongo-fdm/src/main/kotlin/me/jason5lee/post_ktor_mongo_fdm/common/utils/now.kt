package me.jason5lee.post_ktor_mongo_fdm.common.utils

import me.jason5lee.post_ktor_mongo_fdm.common.Time
import java.time.Instant

fun timeFromNow(offset: Long): Time =
    Time.validate(Instant.now().toEpochMilli() + offset).assertValid()
