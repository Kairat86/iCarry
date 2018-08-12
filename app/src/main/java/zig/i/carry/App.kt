package zig.i.carry

import android.app.Application
import io.objectbox.Box
import zig.i.carry.model.Contact
import zig.i.carry.model.MyObjectBox

class App : Application() {

    private lateinit var box: Box<Contact>

    override fun onCreate() {
        super.onCreate()
        box = MyObjectBox.builder().androidContext(this).build().boxFor(Contact::class.java)
    }

    fun getBox() = box
}