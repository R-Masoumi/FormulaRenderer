package masoumi.formularenderer.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import masoumi.formularenderer.R
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkSourceModule {
    @Provides
    @Singleton
    @Named("Url")
    fun provideUrl(@ApplicationContext context: Context): HttpUrl {
        return context.getString(R.string.SERVER_URL).toHttpUrl()
    }

    @Provides
    @Named("FlagNetworkExtensiveLog")
    fun provideLogFlag() = true
}