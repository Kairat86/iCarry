package zig.i.carry.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_verification.*
import kotlinx.android.synthetic.main.pgr_bar_and_ad.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.manager.ApiManager
import zig.i.carry.util.FAILED_TO_CONNECT
import zig.i.carry.util.LOGIN
import zig.i.carry.util.isOK
import zig.i.carry.view.VerificationView
import javax.inject.Inject

class VerificationActivity : DaggerAppCompatActivity(), VerificationView {

    companion object {
        private val TAG: String = VerificationActivity::class.java.simpleName
    }

    @Inject
    lateinit var manager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.sign_up)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_verification)
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adView.visibility = VISIBLE
            }
        }
        adView.loadAd(AdRequest.Builder().build())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        onBackPressed()
        return true
    }

    fun next(v: View) {
        v.isActivated = false
        val email = edtEmail.text.toString()
        when {
            isOK(email.trim()) -> {
                tvErrEmail.text = null
                showLoading()
                manager.validate(email, object : Callback<Boolean> {
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
                            edtEmail.error = getString(R.string.invalid_number)
                        }
                        hideLoading()
                    }
                })
            }
            email.isBlank() -> {
                tvErrEmail.text = getString(R.string.enter_email)
            }
            else -> {
                tvErrEmail.text = getString(R.string.wrong_format)
            }
        }
    }

    fun ok(v: View) {
        val text = edtValidationCode.text.toString()
        if (text.isEmpty()) return
        manager.verify("${edtEmail.text}:$text", object : Callback<Boolean> {
            override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                Log.i(TAG, "resp=>${response?.body()}")
                if (response?.body()!!) {
                    val intent = Intent(this@VerificationActivity, RegisterActivity::class.java)
                    intent.putExtra(LOGIN, edtEmail.text.toString())
                    startActivity(intent)
                    finish()
                } else {
                    edtValidationCodeErr.text = getString(R.string.wrong_code)
                    edtValidationCodeErr.visibility = VISIBLE
                }
            }

        })
    }

    fun showLoading() {
        pb.visibility = VISIBLE
    }

    fun hideLoading() {
        pb.visibility = INVISIBLE
    }


}
