package masoumi.formularenderer.data.persist

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.Dao
import masoumi.formularenderer.data.Formula

/**
 * Room Dao interface , provides methods for persisting formulas
 */
@Dao
interface Dao {

    /**
     * gets formula from the database using its primary key
     * @param formula string representing unformatted formula
     * @return LiveData representing saved formula
     */
    @Query("SELECT * FROM Formula WHERE  formula = :formula LIMIT 1")
    fun loadFormula(formula : String) : LiveData<Formula>

    /**
     * gets all formula that start with phrase
     * @param phrase to search for
     * @return list of formula that start with phrase
     */
    @Query("SELECT * FROM Formula WHERE formula LIKE :phrase || '%' ORDER BY LENGTH(formula) LIMIT 10")
    suspend fun loadFormulasAsync(phrase : String) : List<Formula>

    /**
     * inserts a single formula row
     * @param formula to insert
     * @return row number
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFormula(formula : Formula) : Long

    /**
     * deletes all formula from database
     */
    @Query("DELETE FROM Formula")
    suspend fun deleteFormulas()
}