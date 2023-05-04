package me.jason5lee.post_kt_vertx_fdm.common.utils

import me.jason5lee.post_kt_vertx_fdm.common.Time
import java.time.Instant

fun timeFromNow(offset: Long): me.jason5lee.post_kt_vertx_fdm.common.Time =
    me.jason5lee.post_kt_vertx_fdm.common.Time(Instant.now().toEpochMilli() + offset)
