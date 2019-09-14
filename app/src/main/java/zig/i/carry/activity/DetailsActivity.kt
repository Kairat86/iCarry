package zig.i.carry.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_details.*
import zig.i.carry.R
import zig.i.carry.adapter.AdContactAdapter
import zig.i.carry.model.Ad

class DetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        val ad = intent.getSerializableExtra("ad") as Ad
        tvCountryFrom.text = ad.countryFrom
        tvCityFrom.text = ad.cityFrom
        tvCountryTo.text = ad.countryTo
        tvCityTo.text = ad.cityTo
        tvPrice.append(": ${ad.price} ")
        tvDesc.text = ad.description
        tvCurrency.text = ad.currency
        rvContacts.adapter = AdContactAdapter(ad.contacts?.toMutableList())
    }
}
