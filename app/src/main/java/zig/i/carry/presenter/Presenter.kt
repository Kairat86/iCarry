package zig.i.carry.presenter

import zig.i.carry.view.MVPView

abstract class Presenter<V : MVPView> {

    var view: V? = null
        set(value) {
            field = value
            onViewAttached()
        }

    abstract fun onViewAttached()
    abstract fun showLoading()
    abstract fun hideLoading()

}