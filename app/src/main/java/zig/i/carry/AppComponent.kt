package zig.i.carry

import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import zig.i.carry.injector.ActivityInjector
import zig.i.carry.module.AppModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidInjectionModule::class, AppModule::class, ActivityInjector::class])
interface AppComponent : AndroidInjector<DaggerApplication> {
    @Component.Factory
    abstract class Factory : AndroidInjector.Factory<DaggerApplication>
}