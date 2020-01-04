package zig.i.carry.manager

import retrofit2.Call
import retrofit2.Callback
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import zig.i.carry.manager.ApiManager.Companion.DELETE
import zig.i.carry.manager.ApiManager.Companion.MY_ADS
import zig.i.carry.manager.ApiManager.Companion.OFFERS
import zig.i.carry.manager.ApiManager.Companion.ORDERS
import zig.i.carry.manager.ApiManager.Companion.PUBLISH
import zig.i.carry.manager.ApiManager.Companion.REGISTER
import zig.i.carry.manager.ApiManager.Companion.REMIND
import zig.i.carry.manager.ApiManager.Companion.SIGN_IN
import zig.i.carry.manager.ApiManager.Companion.VALIDATE
import zig.i.carry.manager.ApiManager.Companion.VERIFY
import zig.i.carry.model.Ad
import javax.inject.Inject


class ApiManagerImpl @Inject constructor(private val apiService: APIService) : ApiManager {

    override fun validate(emailOrPhone: String, callback: Callback<Boolean>) {
        apiService.validate(emailOrPhone).enqueue(callback)
    }

    override fun verify(code: String, callback: Callback<Boolean>) {
        apiService.verify(code).enqueue(callback)
    }

    fun register(name: String, pwd: String, email: String, callback: Callback<Boolean>) {
        apiService.register("$name:$pwd:$email").enqueue(callback)
    }

    fun remind(login: String, callback: Callback<String>) {
        apiService.remind(login).enqueue(callback)
    }

    override fun signIn(login: String, pwd: String, callback: Callback<Boolean>) {
        apiService.signIn("$login:$pwd").enqueue(callback)
    }

    override fun publish(ad: Ad, callback: Callback<Boolean>) {
        apiService.publish(ad).enqueue(callback)
    }

    override fun offers(callback: Callback<List<Ad>>) {
        apiService.offers().enqueue(callback)
    }

    override fun orders(callback: Callback<List<Ad>>) {
        apiService.orders().enqueue(callback)
    }

    override fun myAds(login: String?, callback: Callback<List<Ad>>) {
        login?.let { apiService.myAds(it).enqueue(callback) }
    }

    override fun remove(ad: Ad?, callback: Callback<Boolean>) {
        if (ad != null) apiService.delete(ad).enqueue(callback)
    }

    interface APIService {

        @POST(VALIDATE)
        fun validate(@Body s: String): Call<Boolean>

        @POST(VERIFY)
        fun verify(@Body s: String): Call<Boolean>

        @POST(REGISTER)
        fun register(@Body s: String): Call<Boolean>

        @POST(REMIND)
        fun remind(@Body s: String): Call<String>

        @POST(SIGN_IN)
        fun signIn(@Body loginPwd: String): Call<Boolean>

        @POST(PUBLISH)
        fun publish(@Body ad: Ad): Call<Boolean>

        @GET(OFFERS)
        fun offers(): Call<List<Ad>>

        @GET(ORDERS)
        fun orders(): Call<List<Ad>>

        @POST(MY_ADS)
        fun myAds(@Body login: String): Call<List<Ad>>

        @POST(DELETE)
        fun delete(@Body ad: Ad): Call<Boolean>
    }
}
