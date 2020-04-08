package com.lambdaschool.cs_build_week_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import kotlinx.android.synthetic.main.recycler_view_element.view.*

class SelectionAdapter(private val itemList: ArrayList<String>) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_element, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item: String = itemList[position]
        viewHolder.textViewItem.text = item

        if (selectedPosition == position) {
            viewHolder.textViewItem.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorForestFade))
        } else {
            viewHolder.textViewItem.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorCloudFade))
        }
        viewHolder.textViewItem.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition = position
            notifyItemChanged(selectedPosition)
        }
    }

    fun getSelectedItem(): String {
        return itemList[selectedPosition]
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textViewItem: TextView = view.recycler_view_element_text_view_item
    }
}
