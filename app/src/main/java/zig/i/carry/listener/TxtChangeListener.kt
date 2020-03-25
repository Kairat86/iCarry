package zig.i.carry.listener

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.gms.location.places.AutocompleteFilter
import com.google.android.gms.location.places.AutocompleteFilter.TYPE_FILTER_CITIES
import com.google.android.gms.location.places.Places
import zig.i.carry.R
import java.util.*

class TxtChangeListener(private val atv: AutoCompleteTextView, private val list: List<Locale>, private val atvCity: AutoCompleteTextView, private val builder: AutocompleteFilter.Builder) : TextWatcher {

    companion object {
        private var TAG: String = TxtChangeListener::class.java.simpleName
    }

    private var makePredictions = true
    private val client = Places.getGeoDataClient(atv.context)

    override fun afterTextChanged(s: Editable?) {
        Log.i(TAG, "afterTxtChanged=>$s")
        if (atv.text.isBlank()) {
            atv.error = atv.context.getString(R.string.country_name)
            s?.clear()
        } else if (makePredictions) {
            makePredictions = false
            val predictions = client.getAutocompletePredictions(s.toString(), null, builder.build())
            predictions.addOnCompleteListener {
                val mapResult = predictions.result?.map { it.getPrimaryText(null).toString() }
                atvCity.setAdapter(ArrayAdapter<String>(atv.context, android.R.layout.simple_expandable_list_item_1, mapResult?: emptyList()))
            }
        } else if (s?.isBlank()!!) {
            makePredictions = true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val code = list.find { atv.text.toString() == it.displayCountry }?.country
        builder.setCountry(code)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}