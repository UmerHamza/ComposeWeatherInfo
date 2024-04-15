package com.appdev.weathercompose.dal.network

import android.content.Context
import androidx.room.Room
import com.appdev.weathercompose.BuildConfig
import com.appdev.weathercompose.constants.AppConstants
import com.appdev.weathercompose.dal.local.AppDatabase
import com.appdev.weathercompose.dal.services.HomeService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRetrofit(gson: Gson): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(createOkHttpClient())
        .build()

    @Singleton
    @Provides
    fun createOkHttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
            .readTimeout(AppConstants.READ_TIME_OUT, TimeUnit.SECONDS)
            .connectTimeout(AppConstants.CONNECT_TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addInterceptor { chain: Interceptor.Chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .addHeader(
                            AppConstants.AUTHORIZATION,
                            "${AppConstants.BEARER} "
                        )
                        .addHeader(AppConstants.ACCEPT, AppConstants.APPLICATION_TYPE_JSON)
                        .build()
                )
            }
        return httpClient.build()
    }

    @Singleton
    @Provides
    fun provideRoomDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, AppDatabase::class.java, "notesDB")
            .allowMainThreadQueries().fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideNotesDao(db: AppDatabase) = db.notesDao()

    @Singleton
    @Provides
    fun provideGson(): Gson = GsonBuilder().create()

    @Singleton
    @Provides
    fun homeService(retrofit: Retrofit): HomeService = retrofit.create(HomeService::class.java)
}
