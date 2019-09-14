package zig.i.carry.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.ad_contact_item.view.*
import zig.i.carry.R
import zig.i.carry.model.Contact

class AdContactAdapter(private val list: List<Contact>?) : androidx.recyclerview.widget.RecyclerView.Adapter<AdContactAdapter.ContactViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ContactViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ad_contact_item, parent, false))

    override fun getItemCount() = list?.size ?: 0

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        holder.itemView.tvContact.text = list?.get(position)?.value
    }

    class ContactViewHolder(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v)

}
