package masoumi.formularenderer.data.net

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import masoumi.formularenderer.data.CallBody
import masoumi.formularenderer.data.CallResult
import masoumi.formularenderer.data.Formula
import masoumi.formularenderer.data.net.Api.Companion.apiCall
import masoumi.formularenderer.data.persist.Dao
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataSource @Inject constructor(private val api : Api, private val dao : Dao,
                                     @ApplicationContext private val context: Context) {
    fun postCheck(query : String) : LiveData<CallResult<Formula>>{
        return resultLiveData({ dao.loadFormula(query) },
            { apiCall(context = context,
                headerName = "x-resource-location") { api.postCheckAsync(CallBody(query)) } })
        { formula, hash ->
            if(formula?.hash != hash) dao.insertFormula(Formula(query,hash))
        }
    }

    fun getFormulasBlocking(phrase : String) = dao.loadFormulasBlocking(phrase)

    private fun <T, A> resultLiveData(databaseQuery: () -> LiveData<T>,
                                      networkCall: suspend () -> CallResult<A>,
                                      saveCallResult: suspend (T?,A) -> Unit): LiveData<CallResult<T>> =
        liveData(Dispatchers.IO) {
            emit(CallResult.loading())
            val data = databaseQuery()
            val source : LiveData<CallResult<T>> = data.map { CallResult.success(it)}
            emitSource(source)
            val response = networkCall.invoke()
            if (response.isSuccess()) {
                saveCallResult(data.value,response.data!!)
            } else if (response.isFail()) {
                emit(CallResult.error(response.message,response.code))
                emitSource(source)
            }
        }

    private fun <T> resultLiveData(networkCall: suspend () -> CallResult<T>): LiveData<CallResult<T>> =
        liveData(Dispatchers.IO) {
            emit(CallResult.loading())
            val response = networkCall.invoke()
            if (response.isSuccess()) {
                emitSource(MutableLiveData(response.data).map { CallResult.success(it) })
            } else {
                if(response.data != null)
                    emitSource(MutableLiveData(response.data).map {
                        CallResult.error(response.message,response.code,it,response.error) })
                else
                    emit(CallResult.error<T>(response.message,response.code,error = response.error))
            }
        }
}