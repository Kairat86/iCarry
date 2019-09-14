package zig.i.carry

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import zig.i.carry.injector.AdsActivityInjector
import zig.i.carry.injector.LoginActivityInjector
import zig.i.carry.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, AdsActivityInjector::class, LoginActivityInjector::class])
interface AppComponent : AndroidInjector<DaggerApplication> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<DaggerApplication>
}