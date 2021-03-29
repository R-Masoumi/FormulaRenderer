package masoumi.formularenderer.ui.viewmodel

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import masoumi.formularenderer.data.CallResult
import masoumi.formularenderer.data.Formula
import masoumi.formularenderer.data.net.DataSource
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val dataSource : DataSource) : ViewModel(){

    private val liveQuery = MutableLiveData<String>()
    private val liveImage = MutableLiveData<String>()
    private val liveFormula = MutableLiveData<String>()

    val check: LiveData<CallResult<Formula>> = liveQuery.switchMap {
        dataSource.postCheck(it)
    }
    val loading: LiveData<Boolean> = check.map { it.isLoading() }

    fun setQuery(query: String) {
        if (loading.value != true)
            liveQuery.value = query
    }

    fun getQuery() = liveQuery.value

    val formula : LiveData<String> = liveFormula
    val image : LiveData<String> = liveImage

    fun setFormula(formula : String){
        liveFormula.value = formula
    }

    fun setImageHash(hash : String){
        liveImage.value = hash
    }

    suspend fun getFormulasAsync(phrase : String) : List<String>{
        val formulas = dataSource.getFormulasAsync(phrase)
        return formulas.map { it.formula }
    }

}