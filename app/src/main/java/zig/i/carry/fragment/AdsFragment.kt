package zig.i.carry.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
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

open class AdsFragment : Fragment(), Callback<List<Ad>> {

    companion object {
        private val TAG: String = AdsFragment::class.java.simpleName
    }

    private var fragmentView: View? = null
    private var getAds: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
        getAds?.invoke()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (fragmentView == null) fragmentView = inflater.inflate(R.layout.fragment_ads, container, false)
        return fragmentView
    }

    override fun onFailure(call: Call<List<Ad>>?, t: Throwable?) {
        t?.printStackTrace()
        if (t?.localizedMessage?.startsWith(C.FAILED_TO_CONNECT)!!) {
            Toast.makeText(activity, R.string.maintenance, Toast.LENGTH_LONG).show()
            activity?.finish()
        }
    }

    override fun onResponse(call: Call<List<Ad>>?, response: Response<List<Ad>>) {
        prgrBarFragmentAds.visibility = GONE
        rvFragmentAds.adapter = AdAdapter(response.body()?.toMutableList(), false)
    }

    fun setGetAdsFun(f: () -> Unit) {
        this.getAds = f
    }
}