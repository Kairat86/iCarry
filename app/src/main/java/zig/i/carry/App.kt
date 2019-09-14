package zig.i.carry

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

    override fun applicationInjector() = DaggerAppComponent.factory().create(this)

    fun getBox() = box
}