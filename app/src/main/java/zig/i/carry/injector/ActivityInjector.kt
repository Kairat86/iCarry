package zig.i.carry.injector

import dagger.Module
import dagger.android.AndroidInjector
import dagger.android.ContributesAndroidInjector
import dagger.android.support.DaggerAppCompatActivity
import zig.i.carry.activity.*

@Module
interface ActivityInjector : AndroidInjector<DaggerAppCompatActivity> {

    @ContributesAndroidInjector
    fun adsActivity(): AdsActivity

    @ContributesAndroidInjector
    fun loginActivity(): SignInActivity

    @ContributesAndroidInjector
    fun verificationActivity(): VerificationActivity

    @ContributesAndroidInjector
    fun registerActivity(): RegisterActivity

    @ContributesAndroidInjector
    fun adActivity(): AdActivity
}