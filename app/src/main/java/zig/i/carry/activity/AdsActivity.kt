package zig.i.carry.activity

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.MediaStore.Files.FileColumns.TITLE
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_ads.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.App
import zig.i.carry.R
import zig.i.carry.fragment.AdsFragment
import zig.i.carry.fragment.MyAdsFragment
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.Ad
import zig.i.carry.util.IS_LOGGED_IN
import javax.inject.Inject

class AdsActivity : DaggerAppCompatActivity() {

    companion object {
        private val TAG: String = AdsActivity::class.java.simpleName
        private const val REQUEST_CODE_ACTIVITY_FILTER = 1
    }

    @Inject
    lateinit var preferences: SharedPreferences
    lateinit var myAds: List<Ad>
    private lateinit var fragments: MutableList<Fragment>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        tab.setupWithViewPager(pager)
        val offerFragment = AdsFragment()
        var bundle = Bundle()
        bundle.putString(TITLE, getString(R.string.offers))
        offerFragment.arguments = bundle
        offerFragment.setGetAdsFun { ApiManager().offers(offerFragment) }
        bundle = Bundle()
        bundle.putString(TITLE, getString(R.string.orders))
        val orderFragment = AdsFragment()
        orderFragment.arguments = bundle
        orderFragment.setGetAdsFun { ApiManager().orders(orderFragment) }
        fragments = mutableListOf(offerFragment, orderFragment)
        pager.adapter = Adapter(fragments, supportFragmentManager)
        fab.setOnClickListener {
            val intent = if (!preferences.getBoolean(IS_LOGGED_IN, false)) {
                Intent(this, LoginActivity::class.java)
            } else {
                Intent(this, AdActivity::class.java)
            }
            startActivity(intent)
        }

        val login = (application as App).getBox()?.get(1)?.value
        ApiManager().myAds(login, object : Callback<List<Ad>> {
            override fun onFailure(call: Call<List<Ad>>?, t: Throwable?) {
                t?.printStackTrace()
            }

            override fun onResponse(call: Call<List<Ad>>?, response: Response<List<Ad>>?) {
                myAds = response?.body()!!
                if (myAds.isNotEmpty()) {
                    val myAdsFragment = MyAdsFragment()
                    bundle = Bundle()
                    bundle.putString(TITLE, getString(R.string.my_ads))
                    myAdsFragment.arguments = bundle
                    fragments.add(myAdsFragment)
                    pager.adapter?.notifyDataSetChanged()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val isLoggedIn = preferences.getBoolean(IS_LOGGED_IN, false)
        Log.i(TAG, "logged in->$isLoggedIn")
        if (isLoggedIn) {
            menuInflater.inflate(R.menu.menu_ads, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    fun startFilterActivity(item: MenuItem) {
        startActivityForResult(Intent(this, FilterActivity::class.java), REQUEST_CODE_ACTIVITY_FILTER)
    }

    fun logout(item: MenuItem) {
        preferences.edit().putBoolean(IS_LOGGED_IN, false).apply()
        finish()
    }

    inner class Adapter(private val fragments: List<Fragment>, fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int) = fragments[position]

        override fun getCount(): Int = fragments.size

        override fun getPageTitle(position: Int) = fragments[position].arguments?.getString(TITLE)
    }
}


