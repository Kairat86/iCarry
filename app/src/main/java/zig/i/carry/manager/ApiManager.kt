package zig.i.carry.manager

import retrofit2.Callback
import zig.i.carry.model.Ad

interface ApiManager {
    companion object {
        const val VALIDATE = "validate"
        const val VERIFY = "verify";
        const val REGISTER = "register"
        const val REMIND = "remind"
        const val SIGN_IN = "sign_in"
        const val PUBLISH = "publish"
        const val OFFERS = "offers"
        const val ORDERS = "orders"
        const val MY_ADS = "my_ads"
        const val DELETE = "delete"
    }

    fun publish(ad: Ad, callback: Callback<Boolean>)
    fun myAds(login: String?, callback: Callback<List<Ad>>)
    fun offers(callback: Callback<List<Ad>>)
    fun orders(callback: Callback<List<Ad>>)
    fun signIn(login: String, pwd: String, callback: Callback<Boolean>)
    fun remove(ad: Ad?, callback: Callback<Boolean>)
    fun validate(emailOrPhone: String, callback: Callback<Boolean>)
    fun verify(code: String, callback: Callback<Boolean>)
}
