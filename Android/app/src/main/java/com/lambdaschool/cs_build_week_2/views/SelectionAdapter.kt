package com.lambdaschool.cs_build_week_2.views

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.views.SelectionFragment.OnListFragmentInteractionListener
import kotlinx.android.synthetic.main.recycler_view_element.view.*

class SelectionAdapter(
    private val itemList: ArrayList<String>, private val selectionListener: OnListFragmentInteractionListener?
) : RecyclerView.Adapter<SelectionAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as String
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            selectionListener?.onListFragmentInteraction(item)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

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

        with(viewHolder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = itemList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val textViewItem: TextView = view.recycler_view_element_text_view
    }
}
