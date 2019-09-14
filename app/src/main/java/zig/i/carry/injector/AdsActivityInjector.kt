package zig.i.carry.injector

import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import zig.i.carry.activity.AdsActivity

@Module
interface AdsActivityInjector : AndroidInjector<AdsActivity> {

    @ContributesAndroidInjector
    fun adActivity(): AdsActivity
}