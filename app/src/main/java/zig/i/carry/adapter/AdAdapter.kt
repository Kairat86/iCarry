package zig.i.carry.adapter

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ad_item.view.*
import zig.i.carry.R
import zig.i.carry.activity.DetailsActivity
import zig.i.carry.model.Ad
import java.text.SimpleDateFormat

class AdAdapter(private val ads: List<Ad>?) : RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    companion object {
        val TAG: String = AdAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = AdViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.ad_item, parent, false)
    )

    override fun getItemCount() = ads!!.size

    override fun onBindViewHolder(holder: AdViewHolder, position: Int) {
        val ad = ads?.get(position)
        holder.itemView.tvAdItemFrom.text = ad?.cityFrom
        holder.itemView.tvAdItemTo.text = ad?.cityTo
        holder.itemView.tvDate.text = SimpleDateFormat.getDateInstance().format(ad?.createDate)
    }

    inner class AdViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener {
                Log.i(TAG, "open details")
                val context = it.context
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("ad", ads?.get(adapterPosition))
                context.startActivity(intent)
            }
        }
    }

}
