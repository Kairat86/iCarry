package zig.i.carry.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore.Files.FileColumns.TITLE
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_ads.*
import zig.i.carry.R
import zig.i.carry.fragment.AdFragment
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.OfferAd
import zig.i.carry.util.C.IS_LOGGED_IN

class AdsActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = AdsActivity::class.java.simpleName
        private const val REQUEST_CODE_ACTIVITY_FILTER = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads)
        val offerFragment = AdFragment()
        var bundle = Bundle()
        bundle.putString(TITLE, getString(R.string.offers))
        offerFragment.arguments = bundle
        offerFragment.setGetAdsFun { ApiManager().offers(offerFragment) }
        bundle = Bundle()
        bundle.putString(TITLE, getString(R.string.orders))
        val orderFragment = AdFragment()
        orderFragment.arguments = bundle
        orderFragment.setGetAdsFun { ApiManager().orders(orderFragment) }
        val fragments = listOf<Fragment>(offerFragment, orderFragment)
        pager.adapter = Adapter(fragments, supportFragmentManager)
        fab.setOnClickListener {
            startActivity(Intent(this, AdActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ads, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun startFilterActivity(item: MenuItem) {
        startActivityForResult(Intent(this, FilterActivity::class.java), REQUEST_CODE_ACTIVITY_FILTER)
    }

    fun logout(item: MenuItem) {
        getSharedPreferences(packageName + getString(R.string.app_name), Context.MODE_PRIVATE).edit().putBoolean(IS_LOGGED_IN, false).apply()
        finish()
    }
}

class Adapter(private val fragments: List<Fragment>, fragmentManager: android.support.v4.app.FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    override fun getItem(position: Int): Fragment = fragments[position]

    override fun getCount(): Int = fragments.size

    override fun getPageTitle(position: Int) = fragments[position].arguments?.getString(TITLE)
}
