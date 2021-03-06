package zig.i.carry.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import kotlinx.android.synthetic.main.activity_ad_contact_item.view.*
import zig.i.carry.R
import zig.i.carry.model.Contact

class ContactAdapter(private val contacts: MutableList<Contact>?) : androidx.recyclerview.widget.RecyclerView.Adapter<ContactAdapter.VH>() {


    companion object {
        private val TAG: String = ContactAdapter::class.java.simpleName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH = VH(LayoutInflater.from(parent.context).inflate(R.layout.activity_ad_contact_item, parent, false))

    override fun getItemCount(): Int = contacts?.size ?: 0

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.edt.setText(contacts?.get(position)?.value)
        val rId = if (0 == position) {
            android.R.drawable.ic_input_add
        } else {
            android.R.drawable.ic_delete
        }
        holder.itemView.btnDel.setImageResource(rId)
    }


    inner class VH(v: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(v) {

        val edt: EditText = v.findViewById(R.id.edtContact)

        init {
            v.findViewById<ImageView>(R.id.btnDel).setOnClickListener {
                if (contacts?.size ?: 0 > 1) {
                    contacts?.removeAt(adapterPosition)
                    notifyItemRemoved(adapterPosition)
                } else {
                    contacts?.add(Contact())
                    notifyItemInserted(adapterPosition + 1)
                }
            }

            edt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    contacts?.get(adapterPosition)?.value = s.toString()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }
}