package io.tripled.poker.domain

inline fun <reified T> List<Event>.lastEventOrNull() = lastOrNull { it is T } as T?
inline fun <reified T> List<Event>.filterEvents() = filter{ it is T } as List<T>