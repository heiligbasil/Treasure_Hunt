package com.lambdaschool.cs_build_week_2.adapters

import android.annotation.SuppressLint
import android.view.*
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import kotlinx.android.synthetic.main.dialog_combined_element.view.*

class CombinedAdapter(private val itemList: ArrayList<String>, listener: OnRecyclerViewInteractionListener) :
    RecyclerView.Adapter<CombinedAdapter.ViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION
    private val orviListener: OnRecyclerViewInteractionListener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.dialog_combined_element, parent, false)
        return ViewHolder(view, orviListener)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val rawText: String = itemList[viewHolder.adapterPosition]
        if (rawText.contains(':')) {
            val textPrefix = "${rawText.substringBeforeLast(':').trim()}: "
            viewHolder.textView.text = textPrefix
            val llParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
            llParams.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            llParams.weight = 1F
            viewHolder.textView.layoutParams = llParams
            if (viewHolder.editText.text.isEmpty()) {
                viewHolder.editText.setText(rawText.substringAfterLast(':').trim())
            }
            viewHolder.editFrame.visibility = View.VISIBLE
        } else {
            viewHolder.textView.text = rawText
        }
        if (selectedPosition == viewHolder.adapterPosition) {
            viewHolder.layout.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorForestFade))
        } else {
            viewHolder.layout.setBackgroundColor(ContextCompat.getColor(viewHolder.view.context, R.color.colorCloudFade))
        }
    }

    fun getSelectedItem(): String {
        return if (selectedPosition >= 0) {
            itemList[selectedPosition]
        } else {
            ""
        }
    }

    fun getSelectedPosition(): Int = selectedPosition

    override fun getItemViewType(position: Int): Int = position

    override fun getItemCount(): Int = itemList.size

    override fun getItemId(position: Int): Long {
        return selectedPosition.toLong()
    }

    inner class ViewHolder(val view: View, listener: OnRecyclerViewInteractionListener) : RecyclerView.ViewHolder(view) {
        val layout: LinearLayout = view.dialog_combined_element_layout
        val textView: TextView = view.dialog_combined_element_text_view
        val editFrame: FrameLayout = view.dialog_combined_element_frame
        val editText: EditText = view.dialog_combined_element_edit_text
        val editShield: TextView = view.dialog_combined_element_edit_text_shield
        val orvil: OnRecyclerViewInteractionListener = listener

        init {
            editText.showSoftInputOnFocus = false
            view.setOnClickListener {
                notifyItemChanged(selectedPosition)
                if (selectedPosition == layoutPosition && editFrame.visibility == View.VISIBLE && editShield.visibility == View.VISIBLE) {
                    editShield.visibility = View.GONE
                    editText.isEnabled = true
                    editText.requestFocus()
                    editText.append("")
                    editText.setSelection(editText.text.length)
                } else if (selectedPosition != layoutPosition) {
                    editText.clearFocus()
                    textView.requestFocus()
                }
                selectedPosition = layoutPosition
                notifyItemChanged(selectedPosition)
                orvil.recyclerViewItemSelected()
            }
            editShield.setOnClickListener {
                view.callOnClick()
            }
        }
    }

    interface OnRecyclerViewInteractionListener {
        fun recyclerViewItemSelected()
    }
}
