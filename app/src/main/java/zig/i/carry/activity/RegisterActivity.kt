package zig.i.carry.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Intents.Insert.NAME
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.VISIBLE
import kotlinx.android.synthetic.main.activity_register.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.App
import zig.i.carry.R
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.Contact
import zig.i.carry.util.C.IS_LOGGED_IN
import zig.i.carry.util.C.LOGIN

class RegisterActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = RegisterActivity::class.java.simpleName
    }

    private lateinit var manager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        manager = ApiManager()
    }

    fun register(v: View) {
        when {
            edtName.text.isEmpty() -> edtName.error = getString(R.string.type_your_name)
            edtPassword.text.length < 5 -> edtPassword.error = getString(R.string.too_short_pwd)
            edtPassword.text.toString() != edtPasswordRetype.text.toString() -> edtPasswordRetype.error = getString(R.string.passwords_not_match)
            else -> {
                prgrBarActivityRegister.visibility = VISIBLE
                val login = intent.getStringExtra(LOGIN)
                manager.register(edtName.text.toString(), edtPassword.text.toString(), login, object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        t?.printStackTrace()
                    }

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                        val body = response?.body()
                        val intent = Intent(this@RegisterActivity, AdsActivity::class.java)
                        intent.putExtra(NAME, edtName.text)
                        startActivity(intent)
                        val box = (application as App).getBox()
                        box.removeAll()
                        box.put(Contact(login))
                        getSharedPreferences(packageName + getString(R.string.app_name), Context.MODE_PRIVATE)
                                .edit().putBoolean(IS_LOGGED_IN, true).apply()

                        finish()
                    }
                })
            }
        }
    }
}