package com.lambdaschool.cs_build_week_2.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import kotlinx.android.synthetic.main.dialog_selection_element.view.*

class SelectionAdapter(private val itemList: ArrayList<String>, listener: OnRecyclerViewInteractionListener) :
    RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private val orviListener: OnRecyclerViewInteractionListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.dialog_selection_element, parent, false)
        return ViewHolder(view, orviListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val item: String = itemList[position]
        viewHolder.textViewItem.text = item
        if (selectedPosition == position) {
            viewHolder.textViewItem.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorForestFade))
        } else {
            viewHolder.textViewItem.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorCloudFade))
        }
    }

    fun getSelectedItem(): String {
        return if (selectedPosition >= 0) {
            itemList[selectedPosition]
        } else {
            ""
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(val view: View, listener: OnRecyclerViewInteractionListener) : RecyclerView.ViewHolder(view) {
        val textViewItem: TextView = view.dialog_selection_element_text_view
        val orvil: OnRecyclerViewInteractionListener = listener
        init {
            view.setOnClickListener {
                notifyItemChanged(selectedPosition)
                selectedPosition = layoutPosition
                notifyItemChanged(selectedPosition)
                orvil.recyclerViewItemSelected()
            }
        }
    }

    interface OnRecyclerViewInteractionListener {
        fun recyclerViewItemSelected()
    }
}
