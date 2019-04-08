package io.tripled.poker.domain

inline fun <reified T> List<Any>.lastEventOrNull() = lastOrNull { it is T } as T?
inline fun <reified T> List<Any>.filterEvents() = filter{ it is T } as List<T>