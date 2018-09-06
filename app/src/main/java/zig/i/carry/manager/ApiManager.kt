package zig.i.carry.manager

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import zig.i.carry.model.Ad
import java.util.concurrent.TimeUnit


class ApiManager {

    companion object {
        private const val SERVER = "http://46.17.47.168:8080/"
        private const val VALIDATE = "validate"
        private const val VERIFY = "verify";
        private const val REGISTER = "register"
        private const val REMIND = "remind"
        private const val SIGN_IN = "sign_in"
        private const val PUBLISH = "publish"
        private const val OFFERS = "offers"
        private const val ORDERS = "orders"
        private const val MY_ADS = "my-ads"
        private const val DELETE = "delete"
    }

    private var apiService: APIService

    init {
        apiService = Retrofit.Builder()
                .client(OkHttpClient.Builder().readTimeout(15, TimeUnit.SECONDS).build())
                .baseUrl(SERVER)
                .addConverterFactory(JacksonConverterFactory.create())
                .build().create(APIService::class.java)
    }

    fun validate(emailOrPhone: String, callback: Callback<Boolean>) {
        apiService.validate(emailOrPhone).enqueue(callback)
    }

    fun verify(code: String, callback: Callback<Boolean>) {
        apiService.verify(code).enqueue(callback)
    }

    fun register(name: String, pwd: String, email: String, callback: Callback<Boolean>) {
        apiService.register("$name:$pwd:$email").enqueue(callback)
    }

    fun remind(login: String, callback: Callback<String>) {
        apiService.remind(login).enqueue(callback)
    }

    fun signIn(login: String, pwd: String, callback: Callback<Boolean>) {
        apiService.signIn("$login:$pwd").enqueue(callback)
    }

    fun publish(ad: Ad, callback: Callback<Boolean>) {
        apiService.publish(ad).enqueue(callback)
    }

    fun offers(callback: Callback<List<Ad>>) {
        apiService.offers().enqueue(callback)
    }

    fun orders(callback: Callback<List<Ad>>) {
        apiService.orders().enqueue(callback)
    }

    fun myAds(login: String, callback: Callback<List<Ad>>) {
        apiService.myAds(login).enqueue(callback)
    }

    fun remove(ad: Ad?, callback: Callback<Boolean>) {
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
