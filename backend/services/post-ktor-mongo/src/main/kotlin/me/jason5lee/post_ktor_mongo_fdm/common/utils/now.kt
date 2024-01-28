package me.jason5lee.post_ktor_mongo.common.utils

import me.jason5lee.post_ktor_mongo.common.Time
import java.time.Instant

fun timeFromNow(offset: Long): Time =
    Time(Instant.now().toEpochMilli() + offset)
