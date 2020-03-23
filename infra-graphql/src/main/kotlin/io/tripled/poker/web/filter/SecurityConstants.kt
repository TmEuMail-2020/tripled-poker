package io.tripled.poker.web.filter

object SecurityConstants {
    const val EXPIRATION_TIME: Long = 864000000 // 10 days
    const val TOKEN_PREFIX = "Bearer "
    const val HEADER_STRING = "Authorization"
    const val SECRET = ""
}