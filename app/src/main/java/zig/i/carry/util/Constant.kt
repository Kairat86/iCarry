package zig.i.carry.util

import android.content.Context
import android.net.ConnectivityManager
import java.util.regex.Pattern


const val IS_LOGGED_IN = "is_logged_in"
const val FAILED_TO_CONNECT = "Failed to connect to"
const val LOGIN = "login"
const val PREFS_I_CARRY = "prefs_i_carry"


fun isOK(emailOrPhone: String): Boolean {
    val matchesEmail = Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]|[\\w-]{2,}))@((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9]))|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(emailOrPhone).matches()

    val matchesPhone = emailOrPhone.matches(Regex("\\+?[0-9]{10,13}"))
    return matchesEmail || matchesPhone
}

fun isNetworkConnected(ctx: Context) = (ctx.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
