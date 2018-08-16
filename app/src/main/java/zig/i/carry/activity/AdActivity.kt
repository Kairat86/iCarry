package zig.i.carry.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.gson.JsonParser
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
import zig.i.carry.util.C
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class AdActivity : AppCompatActivity() {

    companion object {
        private val TAG: String = AdActivity::class.java.simpleName
        private const val PUBLISH_OK = "publish_ok"
        private const val RESPONSE = "response"
    }

    private val list = Locale.getISOCountries().map { Locale("", it) }
    private lateinit var contacts: MutableList<Contact>
    private lateinit var spinner: Spinner
    private val builder = AutocompleteFilter.Builder().setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ad)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, list.map { it.displayCountry })
        atvCountryFrom.setAdapter(adapter)
        atvCountryTo.setAdapter(adapter)
        atvCityFrom.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) builder.setCountry(list.find { atvCountryFrom.text.toString() == it.displayCountry }?.country) }
        atvCityTo.setOnFocusChangeListener { _, hasFocus -> if (hasFocus) builder.setCountry(list.find { atvCountryTo.text.toString() == it.displayCountry }?.country) }
        atvCityFrom.addTextChangedListener(TxtChangeListener(atvCountryFrom, list, atvCityFrom, builder))
        atvCityTo.addTextChangedListener(TxtChangeListener(atvCountryTo, list, atvCityTo, builder))
        contacts = (application as App).getBox().all
        Log.i(TAG, contacts.toString())
        rvContacts.adapter = ContactAdapter(contacts)
        Thread {
            setCurrencyAdapter()
        }.start()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) onBackPressed()
        return true
    }

    private fun setCurrencyAdapter() {
        val currencies = Currency.getAvailableCurrencies().toMutableList()
        val conn = URL("http://ip-api.com/json").openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.connect()
        val element = JsonParser().parse(InputStreamReader(conn.content as InputStream?))
        val code = (element.asJsonObject).get("countryCode").toString()
        val currency = Currency.getInstance(Locale("", code.substring(1, code.length - 1)))
        currencies.sortBy { it.currencyCode }
        val i = currencies.indexOf(currency)
        val tmp = currencies[0]
        currencies[0] = currency
        currencies[i] = tmp
        val currAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, currencies.map { it.currencyCode })
        currAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        runOnUiThread { spCurrency.adapter = currAdapter }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_ad, menu)
        spinner = menu?.findItem(R.id.spinner)?.actionView as Spinner
        val adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_array, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter;
        return super.onCreateOptionsMenu(menu)
    }

    fun addContact(v: View) {
        if (rvContacts.getChildAt(contacts.size - 1).findViewById<EditText>(R.id.edtContact).text.isNotBlank()) {
            contacts.add(Contact())
            rvContacts.adapter.notifyItemInserted(contacts.size - 1)
        }
    }

    fun publish(item: MenuItem) {
        when {
            atvCityFrom.text.isBlank() -> atvCityFrom.error = getString(R.string.city_name)
            atvCityTo.text.isBlank() -> atvCityTo.error = getString(R.string.city_name)
            else -> {
                val login = (application as App).getBox().all.reduce { e1, e2 -> if (e1.id!! < e2.id!!) e1 else e2 }.value
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
                ad.contacts = contacts.filter { it.value.isNotBlank() }.map {
                    it.id = null
                    it
                }
                Log.i(TAG, "arr after filter=>$contacts")
                ApiManager().publish(ad, object : Callback<Boolean> {
                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                        Log.i(TAG, "failure")
                        if (t?.localizedMessage?.contains(C.FAILED_TO_CONNECT)!!) {
                            Toast.makeText(this@AdActivity, R.string.maintenance, Toast.LENGTH_LONG).show()
                            finish()
                        }
                        t.printStackTrace()
                    }

                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>) {
                        val body = response.body()
                        Log.i(TAG, body.toString())
                        if (body!!) {
                            Toast.makeText(applicationContext, R.string.ad_published, LENGTH_LONG).show()
                            finish()
                            contacts.removeAt(0)
                            (application as App).getBox().put()
                        } else {
                            Toast.makeText(applicationContext, R.string.could_not_publish, LENGTH_LONG).show()
                        }
                    }
                })
            }
        }
    }
}
