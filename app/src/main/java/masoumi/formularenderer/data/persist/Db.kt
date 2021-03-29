package masoumi.formularenderer.data.persist

import androidx.room.Database
import androidx.room.RoomDatabase
import com.squareup.moshi.Moshi
import masoumi.formularenderer.data.Formula

/**
 * Room database
 */
@Database(entities = [Formula::class], version = 1)
abstract class Db : RoomDatabase(){
    abstract fun dao() : Dao
}