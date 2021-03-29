package masoumi.formularenderer.data.persist

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import masoumi.formularenderer.data.Formula

@Dao
interface Dao {

    @Query("SELECT * FROM Formula WHERE  formula = :formula LIMIT 1")
    fun loadFormula(formula : String) : LiveData<Formula>

    @Query("SELECT * FROM Formula")
    fun loadFormulas() : LiveData<List<Formula>>

    @Query("SELECT * FROM Formula WHERE formula LIKE :phrase || '%' ORDER BY LENGTH(formula) LIMIT 10")
    suspend fun loadFormulasAsync(phrase : String) : List<Formula>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormula(formula : Formula) : Long

    @Query("DELETE FROM Formula")
    suspend fun deleteFormulas()
}