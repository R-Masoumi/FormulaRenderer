package masoumi.formularenderer.data

data class CallResult<out T> private constructor(private val status: Status, val data: T?,
                                                 val code : Int? = null,
                                                 val message: String? = null,
                                                 val error : CallError? = null) {

    private enum class Status {
        IDLE,
        SUCCESS,
        ERROR,
        LOADING
    }

    companion object {
        fun <T> idle(data: T?=null, message: String? = null, code: Int? = null): CallResult<T> {
            return CallResult(Status.IDLE, data, code, message)
        }
        fun <T> success(data: T?, message: String? = null, code: Int? = null): CallResult<T> {
            return CallResult(Status.SUCCESS, data, code, message)
        }

        fun <T> error(message: String? = null, code: Int? = null, data : T? = null,error: CallError? = null): CallResult<T> {
            return CallResult(Status.ERROR, data, code, message, error)
        }

        fun <T> loading(data: T? = null): CallResult<T> {
            return CallResult(Status.LOADING, data)
        }
    }

    fun <M> copyConvert(converter : (T?)->M?) =
            CallResult<M>(status,converter(data),code,message)

    fun isIdle() : Boolean =
        status == Status.IDLE

    fun isSuccess() : Boolean =
        status == Status.SUCCESS

    fun isLoading() : Boolean =
        status == Status.LOADING

    fun isFail() : Boolean =
        status == Status.ERROR
}