package zig.i.carry

import android.content.Context
import androidx.multidex.MultiDex
import dagger.android.DaggerApplication
import dagger.android.HasAndroidInjector
import io.objectbox.Box
import zig.i.carry.model.Contact
import zig.i.carry.model.MyObjectBox


class App : DaggerApplication(), HasAndroidInjector {

    private var box: Box<Contact>? = null

    override fun onCreate() {
        super.onCreate()
        box = MyObjectBox.builder().androidContext(this).build().boxFor(Contact::class.java)
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun applicationInjector() = DaggerAppComponent.factory().create(this)

    fun getBox() = box
}