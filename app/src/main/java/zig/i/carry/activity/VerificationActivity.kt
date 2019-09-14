package zig.i.carry.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.manager.ApiManager
import zig.i.carry.util.FAILED_TO_CONNECT
import zig.i.carry.util.LOGIN
import zig.i.carry.util.isOK

class VerificationActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = VerificationActivity::class.java.simpleName
    }

    private lateinit var manager: ApiManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.sign_up)
        setContentView(R.layout.activity_verification)
        manager = ApiManager()
        adViewVerification.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adViewVerification.visibility = VISIBLE
            }
        }
        adViewVerification.loadAd(AdRequest.Builder().build())
    }

    fun next(v: View) {
        v.isActivated = false
        val emailOrPhone = edtEmailOrPhone.text.toString()
        if (isOK(emailOrPhone)) {
            prgrBarActivitySignUp.visibility = VISIBLE
            manager.validate(emailOrPhone, object : Callback<Boolean> {
                override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                    val msg = t?.localizedMessage
                    if (msg?.contains(FAILED_TO_CONNECT)!!) {
                        Toast.makeText(this@VerificationActivity, R.string.maintenance, Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_CANCELED)
                        finish()
                    }
                    t.printStackTrace()
                }

                override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                    Log.i(TAG, response.toString())
                    if (response.body()!!) {
                        edtValidationCode.visibility = VISIBLE
                        btnOK.visibility = VISIBLE
                    } else {
                        edtEmailOrPhone.error = getString(R.string.invalid_number)
                    }
                    prgrBarActivitySignUp.visibility = GONE
                }
            })
        } else {
            edtEmailOrPhone.error = getString(R.string.wrong_format)
        }
    }

    fun ok(v: View) {
        manager.verify("${edtEmailOrPhone.text}:${edtValidationCode.text}", object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                if (response?.body()!!) {
                    val intent = Intent(this@VerificationActivity, RegisterActivity::class.java)
                    intent.putExtra(LOGIN, edtEmailOrPhone.text.toString())
                    startActivity(intent)
                    finish()
                }
            }

        })
    }
}
