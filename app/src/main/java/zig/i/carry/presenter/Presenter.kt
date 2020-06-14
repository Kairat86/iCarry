package zig.i.carry.presenter

abstract class Presenter<V> {

    var view: V? = null
        set(value) {
            field = value
            onViewAttached()
        }

    abstract fun onViewAttached()

}