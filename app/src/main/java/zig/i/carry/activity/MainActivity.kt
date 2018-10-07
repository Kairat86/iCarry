package zig.i.carry.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_sign_in.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.App
import zig.i.carry.R
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.Contact
import zig.i.carry.util.C.FAILED_TO_CONNECT
import zig.i.carry.util.C.IS_LOGGED_IN
import zig.i.carry.util.C.isNetworkConnected
import zig.i.carry.util.C.isOK

class MainActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = MainActivity::class.java.simpleName
        private const val REQUEST_CODE_ACTIVITY_REMIND = 1
        private const val REQUEST_CODE_ACTIVITY_VERIFY = 2
    }

    private lateinit var manager: ApiManager
    private lateinit var preferences: SharedPreferences

    private var isLoggedIn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferences = getSharedPreferences(packageName + getString(R.string.app_name), Context.MODE_PRIVATE)
        isLoggedIn = preferences.getBoolean(IS_LOGGED_IN, false)
        if (isLoggedIn) {
            startActivity(Intent(this, AdsActivity::class.java))
            finish()
            return
        } else {
            setTitle(R.string.sign_in)
            setContentView(R.layout.activity_sign_in)
        }
        manager = ApiManager()
        adViewSignIn.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adViewSignIn.visibility = VISIBLE
            }
        }
        adViewSignIn.loadAd(AdRequest.Builder().build())
    }

    fun signIn(v: View) {
        if (!isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_internet, LENGTH_LONG).show()
            return
        }
        if (edtLogin.text.isEmpty()) {
            edtLogin.error = getString(R.string.enter_login)
            return
        } else if (edtPassword.text.isEmpty()) {
            edtLogin.error = getString(R.string.enter_pwd)
            return
        } else if (!isOK(edtLogin.text.toString())) {
            edtLogin.error = getString(R.string.wrong_format)
            return
        }
        prgrBarActivityMain.visibility = VISIBLE
        manager.signIn(edtLogin.text.toString(), edtPassword.text.toString(), object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                if (t?.localizedMessage?.startsWith(FAILED_TO_CONNECT)!!) {
                    Toast.makeText(this@MainActivity, R.string.maintenance, Toast.LENGTH_LONG).show()
                    finish()
                }
            }

            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                isLoggedIn = response?.body()!!
                if (isLoggedIn) {
                    preferences.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()
                    startActivity(Intent(this@MainActivity, AdsActivity::class.java))
                    finish()
                    val box = (application as App).getBox()
                    if (box.count() == 0L) box.put(Contact(edtLogin.text.toString()))
                } else {
                    Toast.makeText(this@MainActivity, R.string.wrong_creds, LENGTH_LONG).show()
                    prgrBarActivityMain.visibility = GONE
                }
            }
        })
    }

    fun signUp(v: View) {
        startActivityForResult(Intent(this, VerificationActivity::class.java), REQUEST_CODE_ACTIVITY_VERIFY)
    }

    fun remindPwd(v: View) {
        startActivityForResult(Intent(this, RemindPwdActivity::class.java), REQUEST_CODE_ACTIVITY_REMIND)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_CANCELED) finish()
    }
}
