package zig.i.carry.fragment

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_ads.*
import zig.i.carry.activity.AdsActivity
import zig.i.carry.adapter.AdAdapter

class MyAdsFragment : AdsFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvFragmentAds.adapter = AdAdapter((activity as AdsActivity).myAds.toMutableList(), true)
        prgrBarFragmentAds.visibility = View.GONE
    }
}