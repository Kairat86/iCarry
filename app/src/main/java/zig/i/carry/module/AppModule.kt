package zig.i.carry.module

import android.content.Context.MODE_PRIVATE
import dagger.Module
import dagger.Provides
import dagger.android.DaggerApplication
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import zig.i.carry.BuildConfig
import zig.i.carry.manager.ApiManager
import zig.i.carry.manager.ApiManagerImpl
import zig.i.carry.util.PREFS_I_CARRY
import java.util.concurrent.TimeUnit

@Module
class AppModule {

    @Provides
    fun provideSharedPrefs(app: DaggerApplication) = app.getSharedPreferences(PREFS_I_CARRY, MODE_PRIVATE)

    @Provides
    fun provideApiManager(manager: ApiManagerImpl): ApiManager = manager

    @Provides
    fun apiService(): ApiManagerImpl.APIService = Retrofit.Builder()
            .client(OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build())
            .baseUrl(BuildConfig.SERVER)
            .addConverterFactory(JacksonConverterFactory.create())
            .build().create(ApiManagerImpl.APIService::class.java)
}