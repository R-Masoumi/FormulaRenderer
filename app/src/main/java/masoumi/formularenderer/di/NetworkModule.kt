package masoumi.formularenderer.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import masoumi.formularenderer.data.CallError
import masoumi.formularenderer.data.net.Api
import masoumi.formularenderer.util.CallErrorJsonAdapter
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton


/**
 * Dagger-Hilt providers for network components
 */
@Module
@InstallIn(SingletonComponent::class)
class NetworkModule{

    @Provides
    @Singleton
    fun provideLoggingInterceptor(@Named("FlagNetworkExtensiveLog") extensiveLog : Boolean)
            : HttpLoggingInterceptor {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (extensiveLog)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return interceptor
    }

    @Provides
    @Singleton
    fun provideOkHttp(loggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()
    }

    @Provides
    @Singleton
    fun provideMoshi() : Moshi = Moshi.Builder()
            .add(CallError::class.java, CallErrorJsonAdapter().nullSafe()).build()

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: OkHttpClient,@Named("Url") url : HttpUrl, moshi : Moshi): Retrofit {
        val retroBuilder = Retrofit.Builder()
            .client(httpClient)
            .baseUrl(url)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
        return retroBuilder.build()
    }

    @Provides
    @Singleton
    fun provideApi(retrofit: Retrofit): Api {
        return retrofit.create(Api::class.java)
    }
}