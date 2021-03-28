package masoumi.formularenderer.data.persist

import androidx.room.Database
import androidx.room.RoomDatabase
import com.squareup.moshi.Moshi
import masoumi.formularenderer.data.Formula

@Database(entities = [Formula::class], version = 1)
abstract class Db : RoomDatabase(){
    companion object {
        lateinit var moshi: Moshi
    }
    abstract fun dao() : Dao
}