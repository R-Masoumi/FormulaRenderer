package masoumi.formularenderer.ui.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import masoumi.formularenderer.data.CallResult
import masoumi.formularenderer.data.Formula
import masoumi.formularenderer.data.DataSource
import javax.inject.Inject

/**
 * ViewModel for HomeFragment, handles data requests
 * @param dataSource single source of data instance
 */
@HiltViewModel
class HomeViewModel @Inject constructor(private val dataSource : DataSource) : ViewModel(){

    //query to be checked
    private val liveQuery = MutableLiveData<String>()
    //image hash
    private val liveImage = MutableLiveData<String>()
    //formatted formula
    private val liveFormula = MutableLiveData<String>()

    //check data requests, triggered on query change
    val check: LiveData<CallResult<Formula>> = liveQuery.switchMap {
        dataSource.postCheck(it)
    }

    //boolean flag to monitor data check requests
    val loading: LiveData<Boolean> = check.map { it.isLoading() }

    /**
     * Set formula check query
     * @param query string to check
     */
    fun setQuery(query: String) {
        if (loading.value != true)
            liveQuery.value = query
    }

    /**
     * Get formula check query
     * @return query string that was last checked
     */
    fun getQuery() = liveQuery.value

    val formula : LiveData<String> = liveFormula
    val image : LiveData<String> = liveImage

    /**
     * Set formatted formula
     * @param formula string representing formatted formula
     */
    fun setFormula(formula : String){
        liveFormula.value = formula
    }

    /**
     * Set image hash
     * @param hash string representing image hash
     */
    fun setImageHash(hash : String){
        liveImage.value = hash
    }

    /**
     * Get a list of formula similar to phrase, used in suggestion
     * @param phrase string to find similar formula
     * @return list of similar formula strings
     */
    suspend fun getFormulasAsync(phrase : String) : List<String>{
        val formulas = dataSource.getFormulasAsync(phrase)
        return formulas.map { it.formula }
    }
}