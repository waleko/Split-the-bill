package me.alexkovrigin.splitthebill.util

/**
 * A generic class that holds a value with its loading status.
 * @param <T>
 */
sealed class Result<out T : Any> {

    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }

    /**
     * Effectively casts [Result] to [Success]. If cast has failed, throws stored [Error.exception] of the result.
     */
    fun orThrowException(): Success<T> {
        return when(this) {
            is Success -> this
            is Error -> throw exception
        }
    }

    /**
     * True if result is [Success], false otherwise.
     */
    val isSuccess
        get() = this is Success

    /**
     * True if result is [Error], false otherwise.
     */
    val isError
        get() = !isSuccess

}