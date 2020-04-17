package com.lambdaschool.cs_build_week_2.dialogs

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.adapters.CombinedAdapter
import kotlinx.android.synthetic.main.dialog_combined.view.*
import java.util.*


class CombinedDialog : DialogFragment(), CombinedAdapter.OnRecyclerViewInteractionListener {

    companion object {
        const val listTag = "the_list"
        const val titleTag = "title_text"
        const val colorTag = "tint_color"
    }

    private var listener: OnCombinedDialogInteractionListener? = null
    private var listCombined: ArrayList<String> = arrayListOf()
    private var tintColor: Int = R.color.colorAmber
    private var titleText: String = "Select the Option"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCombinedDialogInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnCombinedDialogInteractionListener")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            listCombined = arguments?.getStringArrayList(listTag) ?: listCombined
            titleText = arguments?.getString(titleTag) ?: titleText
            tintColor = arguments?.getInt(colorTag) ?: tintColor
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_combined, container, false)
        with(view) {
            backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(context, tintColor))
            dialog_combined_text_view_title.text = titleText
        }
        with(view.dialog_combined_recycler_view_container) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter?.setHasStableIds(true)
            adapter = CombinedAdapter(listCombined, this@CombinedDialog)
            val dividerDecorator = DividerItemDecoration(context, RecyclerView.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.divider)?.let { dividerDecorator.setDrawable(it) }
            addItemDecoration(dividerDecorator)
        }
        view.dialog_combined_button_cancel.setOnClickListener {
            this.dismiss()
        }
        view.dialog_combined_button_confirm.setOnClickListener {
            val adapter: CombinedAdapter = view.dialog_combined_recycler_view_container.adapter as CombinedAdapter
            val selectedPosition: Int = adapter.getSelectedPosition()
            val viewHolder =
                view.dialog_combined_recycler_view_container.findViewHolderForAdapterPosition(selectedPosition) as CombinedAdapter.ViewHolder
            var number: Int? = null
            if (viewHolder.editFrame.visibility == View.VISIBLE) {
                number = viewHolder.editText.text.toString().toInt()
            }
            listener?.onCombinedDialogInteractionAutomation(number)
            this.dismiss()
        }
        return view
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun recyclerViewItemSelected() {
        with(view?.dialog_combined_button_confirm) {
            this?.isEnabled = true
            this?.backgroundTintList = ContextCompat.getColorStateList(this@CombinedDialog.requireContext(), R.color.colorForest)
        }
    }

    interface OnCombinedDialogInteractionListener {
        fun onCombinedDialogInteractionAutomation(destinationRoomId: Int? = null)
    }
}
