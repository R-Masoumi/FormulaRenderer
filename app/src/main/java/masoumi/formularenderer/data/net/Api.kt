package masoumi.formularenderer.data.net

import android.content.Context
import com.squareup.moshi.Moshi
import masoumi.formularenderer.R
import masoumi.formularenderer.data.CallBody
import masoumi.formularenderer.data.CallError
import masoumi.formularenderer.data.CallResult
import masoumi.formularenderer.data.SuccessResult
import masoumi.formularenderer.util.MessageUtils
import masoumi.formularenderer.util.MessageUtils.sendMessageBroadcast
import okhttp3.internal.toHeaderList
import retrofit2.Response
import retrofit2.http.*
import java.lang.Exception

/**
 * Retrofit Api interface
 */
interface Api{
    /**
     * Check formula on wiki Rest Api and returns image hash in header
     * @param info CallBody object which is the json request body
     * @return request response
     */
    @POST("media/math/check/tex")
    suspend fun postCheckAsync(@Body info : CallBody) : Response<SuccessResult>

    @Suppress("BlockingMethodInNonBlockingContext")
    companion object {
        /**
         * Helper method which can be used to safely do an API call from the interface,
         * This method will automatically send error message broadcasts on failure and
         * handle exceptions.
         * @param context application context
         * @param moshi moshi instance used to get adapter for handling error json
         * @param call the actual suspending network call
         * @return CallResult instance with transformed header and data
         */
        suspend fun <T : Any> apiCall(context : Context, moshi: Moshi,
                                      call: suspend () -> Response<T>): CallResult<T> {
            try{
                val response = call.invoke()
                val body = response.body()
                val errorBody = response.errorBody()
                var errorObj : CallError? = null
                if (response.isSuccessful) {
                    val headers = response.headers().toHeaderList()
                            .map { it.name.utf8() to it.value.utf8() }.toMap()
                    return CallResult.success(body, headers,"",response.code())
                }
                if(errorBody != null){
                    val adapter = moshi.adapter(CallError::class.java)
                    errorObj = adapter.fromJson(errorBody.string())
                    sendMessageBroadcast(
                        context,
                        errorObj?.error ?: context.getString(R.string.msg_connection_error),
                        MessageUtils.MessageType.ERROR)
                }
                return CallResult.error(response.message(), response.code(),null, error = errorObj)
            }catch (e : Exception){
                return CallResult.error(e.message ?: e.toString())
            }
        }
    }
}