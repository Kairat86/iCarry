package zig.i.carry.adapter

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ad_item.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import zig.i.carry.R
import zig.i.carry.activity.DetailsActivity
import zig.i.carry.manager.ApiManager
import zig.i.carry.model.Ad
import java.io.Serializable
import java.text.SimpleDateFormat
import javax.inject.Inject

open class AdAdapter(private val ads: MutableList<Ad>?, private val isMyAds: Boolean) : androidx.recyclerview.widget.RecyclerView.Adapter<AdAdapter.AdViewHolder>() {

    @Inject
    lateinit var apiManager: ApiManager

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

    fun add(s: Serializable?) {
        ads?.add(0, s as Ad)
        notifyItemInserted(0)
    }

    inner class AdViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {
        init {
            v.setOnClickListener {
                Log.i(TAG, "open details")
                val context = it.context
                val intent = Intent(context, DetailsActivity::class.java)
                intent.putExtra("ad", ads?.get(adapterPosition))
                context.startActivity(intent)
            }
            if (isMyAds) {
                v.setOnLongClickListener {
                    AlertDialog.Builder(it.context).setMessage(R.string.are_you_sure_to_del)
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes) { d, _ ->
                                d.dismiss()
                                apiManager.remove(ads?.get(adapterPosition), object : Callback<Boolean> {
                                    override fun onFailure(call: Call<Boolean>?, t: Throwable?) {
                                        t?.printStackTrace()
                                    }

                                    override fun onResponse(call: Call<Boolean>?, response: Response<Boolean>?) {
                                        Log.i(TAG, "remove=>${response?.body()}")
                                    }
                                })
                                val index = adapterPosition
                                ads?.removeAt(index)
                                notifyItemRemoved(index)
                            }.create().show()
                    true
                }
            }
        }
    }

}
