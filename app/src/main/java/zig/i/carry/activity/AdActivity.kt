package zig.i.carry.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.annotation.RequiresApi
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.widget.Autocomplete
import dagger.android.support.DaggerAppCompatActivity
import kotlinx.android.synthetic.main.activity_ad.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.App
import zig.i.carry.R
import zig.i.carry.adapter.ContactAdapter
import zig.i.carry.listener.TxtChangeListener
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.Contact
import zig.i.carry.model.OfferAd
import zig.i.carry.model.OrderAd
import zig.i.carry.util.FAILED_TO_CONNECT
import java.util.*
import javax.inject.Inject


class AdActivity : DaggerAppCompatActivity() {

    companion object {
        private val TAG: String = AdActivity::class.java.simpleName
    }

    private val list = Locale.getISOCountries().map { Locale("", it) }
    private var contacts: MutableList<Contact>? = null
    private lateinit var spinner: Spinner
    private val builder = FindAutocompletePredictionsRequest.builder().setTypeFilter(TypeFilter.CITIES)

    @Inject
    lateinit var preferences: SharedPreferences

    @Inject
    lateinit var apiManager: ApiManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Places.initialize(applicationContext, getString(R.string.KEY))
        preferences = getSharedPreferences(packageName + getString(R.string.app_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_ad)
        val countries = list.map { it.displayCountry }
        val adapter = ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, countries)
        atvCountryFrom.setAdapter(adapter)
        atvCountryTo.setAdapter(adapter)
        atvCityFrom.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) builder.setCountry(list.find { atvCountryFrom.text.toString() == it.displayCountry }?.country) }
        atvCityTo.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) builder.setCountry(list.find { atvCountryTo.text.toString() == it.displayCountry }?.country) }
        atvCityFrom.addTextChangedListener(TxtChangeListener(atvCountryFrom, list, atvCityFrom, builder))
        atvCityTo.addTextChangedListener(TxtChangeListener(atvCountryTo, list, atvCityTo, builder))
        contacts = (application as App).getBox()?.all
        rvContacts.adapter = ContactAdapter(contacts)
        setCurrencyAdapter()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) onBackPressed()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun setCurrencyAdapter() {
        val currencies = Currency.getAvailableCurrencies().toMutableList()

        val tm = this.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        val code = tm.networkCountryIso
        val currency = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Currency.getInstance(Locale.Builder().setRegion(code).build())
        } else {
            Currency.getInstance(Locale("", code))
        }
        currencies.sortBy { it.currencyCode }
        val i = currencies.indexOf(currency)
        val tmp = currencies[0]
        currencies[0] = currency
        currencies[i] = tmp
        val currAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, currencies.map { it.currencyCode })
        currAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCurrency.adapter = currAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ad, menu)
        spinner = menu?.findItem(R.id.spinner)?.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        return super.onCreateOptionsMenu(menu)
    }

    fun addContact(v: View) {
        if (rvContacts.getChildAt(contacts!!.size - 1).findViewById<EditText>(R.id.edtContact).text.isNotBlank()) {
            contacts?.add(Contact())
            rvContacts.adapter?.notifyItemInserted(contacts!!.size - 1)
        }
    }

    fun publish(item: MenuItem) {
        when {
            atvCityFrom.text.isBlank() -> atvCityFrom.error = getString(R.string.city_name)
            atvCityTo.text.isBlank() -> atvCityTo.error = getString(R.string.city_name)
            else -> {
                val login = (application as App).getBox()?.all?.reduce { e1, e2 -> if (e1.id!! < e2.id!!) e1 else e2 }?.value
                val selectedItemPosition = spinner.selectedItemPosition
                val ad = if (selectedItemPosition == 0) OfferAd() else OrderAd()
                ad.countryFrom = atvCountryFrom.text.toString()
                ad.countryTo = atvCountryTo.text.toString()
                ad.cityFrom = atvCityFrom.text.toString()
                ad.cityTo = atvCityTo.text.toString()
                ad.description = edtDesc.text.toString()
                ad.price = edtPrice.text.toString()
                ad.currency = spCurrency.selectedItem.toString()
                ad.userLogin = login
                ad.contacts = contacts?.filter { it.value.isNotBlank() }?.map {
                    it.id = null
                    it
                }
                apiManager.publish(ad, object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        if (t?.localizedMessage?.contains(FAILED_TO_CONNECT)!!) {
                            Toast.makeText(this@AdActivity, R.string.maintenance, LENGTH_LONG).show()
                            finish()
                        }
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>) {
                        val body = response.body()
                        Log.i(TAG, body.toString())
                        if (body != null && body) {
                            Toast.makeText(applicationContext, R.string.ad_published, LENGTH_LONG).show()
                            finish()
                            contacts?.removeAt(0)
                            (application as App).getBox()?.put()
                        } else {
                            Toast.makeText(applicationContext, R.string.could_not_publish, LENGTH_LONG).show()
                        }
                    }
                })
            }
        }
    }
}
