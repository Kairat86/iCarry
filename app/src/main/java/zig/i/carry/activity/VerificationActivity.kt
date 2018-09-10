package zig.i.carry.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_verification.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.manager.ApiManager
import zig.i.carry.util.C
import zig.i.carry.util.C.LOGIN
import zig.i.carry.util.C.isOK

class VerificationActivity : AppCompatActivity() {
    companion object {
        private val TAG: String = VerificationActivity::class.java.simpleName
        private lateinit var callMeMenuItem: MenuItem
    }

    private lateinit var manager: ApiManager
    private var seconds = 120;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTitle(R.string.sign_up)
        setContentView(R.layout.activity_verification)
        manager = ApiManager()
    }

//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.menu_verification, menu)
//        callMeMenuItem = menu.findItem(R.id.action_call_me)
//        return true
//    }

    fun call(item: MenuItem) {

    }

    fun next(v: View) {
        v.isActivated = false
        val emailOrPhone = edtEmailOrPhone.text.toString()
        if (isOK(emailOrPhone)) {
            prgrBarActivitySignUp.visibility = VISIBLE
            manager.validate(emailOrPhone, object : Callback<Boolean> {
                override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                    val msg = t?.localizedMessage
                    if (msg?.contains(C.FAILED_TO_CONNECT)!!) {
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
//                        callMeMenuItem.isVisible = true
//                        startTimeCounter()
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

    private fun startTimeCounter() {
        callMeMenuItem.title = seconds--.toString()
        Handler().postDelayed({
            if (seconds > 0) {
                startTimeCounter()
            } else {
                callMeMenuItem.title = getString(R.string.call_me)
            }
        }, 1000)
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
