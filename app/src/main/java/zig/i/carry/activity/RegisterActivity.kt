package zig.i.carry.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.View.VISIBLE
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.pgr_bar_and_ad.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.App
import zig.i.carry.R
import zig.i.carry.manager.ApiManagerImpl
import zig.i.carry.model.Contact
import zig.i.carry.util.IS_LOGGED_IN
import zig.i.carry.util.LOGIN
import javax.inject.Inject

class RegisterActivity : DaggerAppCompatActivity() {

    companion object {
        private val TAG: String = RegisterActivity::class.java.simpleName
    }

    @Inject
    lateinit var manager: ApiManagerImpl
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = VISIBLE
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    fun register(v: View) {
        when {
            edtName.text.isEmpty() -> edtName.error = getString(R.string.type_your_name)
            edtPassword.text.length < 5 -> edtPassword.error = getString(R.string.too_short_pwd)
            edtPassword.text.toString() != edtPasswordRetype.text.toString() -> edtPasswordRetype.error = getString(R.string.passwords_not_match)
            else -> {
                pb.visibility = VISIBLE
                val login = intent.getStringExtra(LOGIN)
                manager.register(edtName.text.toString(), edtPassword.text.toString(), login, object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        t?.printStackTrace()
                    }

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                        val intent = Intent(this@RegisterActivity, AdsActivity::class.java)
                        startActivity(intent)
                        val box = (application as App).getBox()
                        box?.removeAll()
                        box?.put(Contact(login))
                        sharedPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply()
                        finish()
                    }
                })
            }
        }
    }
}