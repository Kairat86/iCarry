package zig.i.carry.listener

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import zig.i.carry.R
import java.util.*

class TxtChangeListener(private val atv: AutoCompleteTextView, private val list: List<Locale>, private val atvCity: AutoCompleteTextView, private val builder: FindAutocompletePredictionsRequest.Builder) : TextWatcher {

    companion object {
        private var TAG: String = TxtChangeListener::class.java.simpleName
    }

    private var makePredictions = true
    private val client = Places.createClient(atv.context)

    override fun afterTextChanged(s: Editable?) {
        when {
            atv.text.isBlank() -> {
                atv.error = atv.context.getString(R.string.country_name)
                s?.clear()
            }
            makePredictions -> {
                makePredictions = false
                val task = client.findAutocompletePredictions(builder.setQuery(s.toString()).build())
                task.addOnCompleteListener { t ->
                    val mapResult = t.result?.autocompletePredictions?.map { it.getPrimaryText(null).toString() }
                    atvCity.setAdapter(ArrayAdapter(atv.context, android.R.layout.simple_expandable_list_item_1, mapResult
                            ?: emptyList()))
                }
            }
            s?.isBlank()!! -> {
                makePredictions = true
            }
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        val code = list.find { atv.text.toString() == it.displayCountry }?.country
        builder.setCountry(code)
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}