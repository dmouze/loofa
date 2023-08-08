package com.example.loofa.util

open class Event<out T>(val content: T) {
    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) { // Jeśli zdarzenie zostało już obsłużone
            null // Zwróć null,
        } else { // W przeciwnym razie
            hasBeenHandled = true // Oznacz zdarzenie jako obsłużone
            content // Zwróć zawartość
        }
    }

    /**
     * Zwraca zawartość zdarzenia bez względu na to, czy zostało już obsłużone.
     */
    fun peekContent(): T = content
}
