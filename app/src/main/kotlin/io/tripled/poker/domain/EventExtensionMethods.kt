package io.tripled.poker.domain

inline fun <reified T> List<Event>.lastEventOrNull() = lastOrNull { it is T } as T?
inline fun <reified T> List<Event>.filterEvents() = filterIsInstance<T>()
inline fun <reified T> List<Event>.ifContaining(action: () -> String): String {
    if (this.filterIsInstance<T>().isNotEmpty()){
        return action()
    }
    return ""
}