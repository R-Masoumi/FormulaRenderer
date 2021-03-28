package masoumi.formularenderer.data.net

import android.content.Context
import com.squareup.moshi.Moshi
import masoumi.formularenderer.R
import masoumi.formularenderer.data.CallBody
import masoumi.formularenderer.data.CallError
import masoumi.formularenderer.data.CallResult
import masoumi.formularenderer.util.MessageUtils
import masoumi.formularenderer.util.MessageUtils.sendMessageBroadcast
import retrofit2.Response
import retrofit2.http.*
import java.lang.Exception

interface Api{
    @POST("media/math/check/tex")
    suspend fun postCheckAsync(@Body info : CallBody) : Response<Unit>

    companion object {
        suspend fun apiCall(context : Context, headerName : String,
                                      call: suspend () -> Response<out Any>): CallResult<String> {
            try{
                val response = call.invoke()
                val errorBody = response.errorBody()
                var errorObj : CallError? = null
                if (response.isSuccessful) {
                    return CallResult.success(response.headers()[headerName],"",response.code())
                }
                if(errorBody != null){
                    val adapter = Moshi.Builder().build().adapter(CallError::class.java)
                    errorObj = adapter.fromJson(errorBody.string())
                    sendMessageBroadcast(
                        context,
                        errorObj?.error ?: context.getString(R.string.msg_connection_error),
                        MessageUtils.MessageType.ERROR)
                }
                return CallResult.error(response.message(), response.code(),null,errorObj)
            }catch (e : Exception){
                sendMessageBroadcast(
                    context,
                    context.getString(R.string.msg_connection_error),
                    MessageUtils.MessageType.ERROR)
                return CallResult.error(e.message ?: e.toString())
            }
        }

        suspend fun <T : Any> apiCall(context : Context,
                                      call: suspend () -> Response<T>): CallResult<T> {
            try{
                val response = call.invoke()
                val body = response.body()
                val errorBody = response.errorBody()
                var errorObj : CallError? = null
                if (response.isSuccessful) {
                    return CallResult.success(body,"",response.code())
                }
                if(errorBody != null){
                    val adapter = Moshi.Builder().build().adapter(CallError::class.java)
                    errorObj = adapter.fromJson(errorBody.string())
                    sendMessageBroadcast(
                        context,
                        errorObj?.error ?: context.getString(R.string.msg_connection_error),
                        MessageUtils.MessageType.ERROR)
                }
                return CallResult.error(response.message(), response.code(),null,errorObj)
            }catch (e : Exception){
                return CallResult.error(e.message ?: e.toString())
            }
        }
    }
}