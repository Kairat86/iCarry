package zig.i.carry.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_ads.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.adapter.AdAdapter
import zig.i.carry.model.Ad
import zig.i.carry.util.C

class AdFragment : Fragment(), Callback<List<Ad>> {

    companion object {
        private val TAG: String = AdFragment::class.java.simpleName
    }

    private lateinit var getAds: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        getAds()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = inflater
            .inflate(R.layout.fragment_ads, container, false)

    override fun onFailure(call: Call<List<Ad>>?, t: Throwable?) {
        t?.printStackTrace()
        if (t?.localizedMessage?.startsWith(C.FAILED_TO_CONNECT)!!) {
            Toast.makeText(activity, R.string.maintenance, Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onResponse(call: Call<List<Ad>>?, response: Response<List<Ad>>) {
        Log.i(TAG, "response=>${response.body()}")
        prgrBarFragmentAds.visibility = GONE
        rvFragmentAds.adapter = AdAdapter(response.body())
    }

    fun setGetAdsFun(f: () -> Unit) {
        this.getAds = f
    }
}
