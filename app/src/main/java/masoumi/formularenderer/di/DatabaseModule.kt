package masoumi.formularenderer.di

import android.content.Context
import androidx.room.Room
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import masoumi.formularenderer.data.persist.Dao
import masoumi.formularenderer.data.persist.Db
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context, moshi: Moshi): Db {
        return Room.databaseBuilder(context,
            Db::class.java, "cache").fallbackToDestructiveMigration().build().apply { Db.moshi = moshi }
    }

    @Provides
    @Singleton
    fun provideDao(db : Db): Dao {
        return db.dao()
    }
}