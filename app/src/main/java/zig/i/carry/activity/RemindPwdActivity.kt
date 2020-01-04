package zig.i.carry.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.VISIBLE
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_remind.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.manager.ApiManagerImpl
import zig.i.carry.util.FAILED_TO_CONNECT
import zig.i.carry.util.isNetworkConnected
import zig.i.carry.util.isOK
import javax.inject.Inject

class RemindPwdActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = RemindPwdActivity::class.java.simpleName
        private const val PASSWORD_SENT = "PASSWORD_SENT"
        private const val REMIND_ERROR = "REMIND_ERROR"
    }

    @Inject
    lateinit var manager: ApiManagerImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remind)
    }

    fun remind(v: View) {
        if (!isNetworkConnected(this)) {
            Toast.makeText(this, R.string.no_internet, LENGTH_LONG).show()
            return
        }
        if (!isOK(edtEmailOrPhone.text.toString())) {
            edtEmailOrPhone.error = getString(R.string.wrong_format)
            return
        }
        prgrBarActivityRemind.visibility = VISIBLE
        manager.remind(edtEmailOrPhone.text.toString(), object : Callback<String> {
            override fun onFailure(call: Call<String>?, t: Throwable?) {
                val msg = t?.localizedMessage
                val contains = msg?.contains(FAILED_TO_CONNECT)!!
                Log.i(TAG, "contains=>$contains")
                if (contains) {
                    Toast.makeText(this@RemindPwdActivity, R.string.maintenance, LENGTH_LONG).show()
                    setResult(Activity.RESULT_CANCELED)
                    finish()
                }
            }

            override fun onResponse(call: Call<String>?, response: Response<String>?) {
                val body = response?.body()
                when {
                    body.equals(PASSWORD_SENT) -> Toast.makeText(this@RemindPwdActivity, R.string.check_email, LENGTH_LONG).show()
                    body.equals(REMIND_ERROR) -> Toast.makeText(this@RemindPwdActivity, R.string.email_not_sent, LENGTH_LONG).show()
                    else -> {
                        Toast.makeText(this@RemindPwdActivity, R.string.not_registered, LENGTH_LONG).show()
                    }
                }
                setResult(Activity.RESULT_OK)
                finish()
            }
        })
    }
}
