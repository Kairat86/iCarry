package zig.i.carry.module

import android.content.Context.MODE_PRIVATE
import dagger.Module
import dagger.Provides
import dagger.android.DaggerApplication
import zig.i.carry.util.PREFS_I_CARRY
import javax.inject.Singleton

@Module
class AppModule {

    @Singleton
    @Provides
    fun provideSharedPrefs(app: DaggerApplication) = app.getSharedPreferences(PREFS_I_CARRY, MODE_PRIVATE)
}