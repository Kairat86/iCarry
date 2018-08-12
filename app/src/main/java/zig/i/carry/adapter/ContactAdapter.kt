package zig.i.carry.adapter

import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_ad_contact_item.view.*
import zig.i.carry.R
import zig.i.carry.model.Contact

class ContactAdapter(private val contacts: MutableList<Contact>) : RecyclerView.Adapter<ContactAdapter.VH>() {


    companion object {
        private val TAG: String = ContactAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(LayoutInflater.from(parent.context).inflate(R.layout.activity_ad_contact_item, parent, false))

    override fun getItemCount(): Int = contacts.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.edt.setText(contacts[position].value)
        if (contacts.size == 1) {
            holder.itemView.btnDel.visibility = GONE
        }
    }


    inner class VH(v: View) : RecyclerView.ViewHolder(v) {

        val edt: EditText = v.findViewById(R.id.edtContact)

        init {
            v.findViewById<ImageView>(R.id.btnDel).setOnClickListener {
                Log.i(TAG, "del")
                contacts.removeAt(adapterPosition)
                notifyItemRemoved(adapterPosition)
            }

            edt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    contacts[adapterPosition].value = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}