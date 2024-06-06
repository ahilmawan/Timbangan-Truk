package id.ahilmawan.weightbridge.models

/**
 * A generic class that holds a value or error.
 * @param [T]
 */
sealed class Resource<T> {

    /**
     * Represent data loading state
     */
    class Loading<T> : Resource<T>()

    /**
     * A data class to wrap the success value
     */
    data class Success<T>(val data: T) : Resource<T>()

    /**
     * A data class to wrap [Throwable] value in case
     * of error
     */
    data class Failure<T>(val error: Throwable) : Resource<T>()
}
