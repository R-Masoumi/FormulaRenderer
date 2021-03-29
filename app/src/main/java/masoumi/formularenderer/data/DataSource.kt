package masoumi.formularenderer.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.map
import com.squareup.moshi.Moshi
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import masoumi.formularenderer.data.net.Api
import masoumi.formularenderer.data.net.Api.Companion.apiCall
import masoumi.formularenderer.data.persist.Dao
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This singleton handles all data requests as a single source using liveData,
 * automatically fetches databased and network data without caller engagement
 *
 * @param api retrofit api instance
 * @param dao room database dao instance
 * @param moshi json adapter handler
 * @param context application context
 */
@Singleton
class DataSource @Inject constructor(private val api : Api, private val dao : Dao,
                                     private val moshi: Moshi,
                                     @ApplicationContext private val context: Context) {
    /**
     * Check formula request which handles fetching formula object from a query
     * @param query string that represents on formatted formula
     * @return LiveData that will update when data is available
     */
    fun postCheck(query : String) : LiveData<CallResult<Formula>>{
        return resultLiveData({ dao.loadFormula(query) },
            { apiCall(context = context, moshi = moshi) { api.postCheckAsync(CallBody(query)) } })
        { formula, result , extra ->
            val hash = extra["x-resource-location"]
            if(formula?.hash != hash) dao.insertFormula(Formula(query,hash, result.checked))
        }
    }

    /**
     * get a list of saved formula that resemble the phrase,
     * this is a suspend function as the list will not be readily available
     * @param phrase string to get similar formulas
     * @return list of all formula that resemble the phrase
     */
    suspend fun getFormulasAsync(phrase : String) = dao.loadFormulasAsync(phrase)

    /**
     * Helper method that executes and emits database and network calls as a single liveData
     * @param databaseQuery method containing database call invocation
     * @param networkCall method containing network call invocation
     * @param saveCallResult method responsible for persisting network results to database
     * @return LiveData that will report data state
     */
    private fun <T, A> resultLiveData(databaseQuery: () -> LiveData<T>,
                                      networkCall: suspend () -> CallResult<A>,
                                      saveCallResult: suspend (T?,A,extra : Map<String,String>) -> Unit): LiveData<CallResult<T>> =
        liveData(Dispatchers.IO) {
            emit(CallResult.loading())
            val data = databaseQuery()
            val source : LiveData<CallResult<T>> = data.map { CallResult.success(it)}
            emitSource(source)
            val response = networkCall.invoke()
            if (response.isSuccess()) {
                saveCallResult(data.value,response.data!!, response.extra)
            } else if (response.isFail()) {
                emit(CallResult.error(response.message,response.code))
                emitSource(source)
            }
        }

    /**
     * Helper method that executes and emits network only calls as a liveData
     * @param networkCall method containing network call invocation
     * @return LiveData that will report data state
     */
    private fun <T> resultLiveData(networkCall: suspend () -> CallResult<T>): LiveData<CallResult<T>> =
        liveData(Dispatchers.IO) {
            emit(CallResult.loading())
            val response = networkCall.invoke()
            if (response.isSuccess()) {
                emitSource(MutableLiveData(response))
            } else {
                if(response.data != null)
                    emitSource(MutableLiveData(response.data).map {
                        CallResult.error(response.message, response.code, it, error = response.error)})
                else
                    emit(CallResult.error<T>(response.message, response.code, error = response.error))
            }
        }
}