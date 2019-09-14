package zig.i.carry.injector

import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import zig.i.carry.activity.LoginActivity


@Module
interface LoginActivityInjector : AndroidInjector<LoginActivity> {
    @ContributesAndroidInjector
    fun loginActivity(): LoginActivity
}