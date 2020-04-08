package com.lambdaschool.cs_build_week_2.views

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.adapters.SelectionAdapter
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.dialog_selection.view.*
import java.util.*


class SelectionDialog : DialogFragment() {

    companion object {
        const val selectionTag = "item_list"
        const val colorTag = "tint_color"
        const val enumTag = "enum_selection"
    }

    private var listener: OnSelectionDialogInteractionListener? = null
    private var listSelection: ArrayList<String> = arrayListOf()
    private var tintColor: Int = R.color.colorAmber
    private var enumSelection: Selections = Selections.NONE

    @Parcelize
    enum class Selections : Parcelable {
        NONE, TAKE, DROP, BUY, SELL, WEAR, UNDRESS, EXAMINE, DASH, TRANSMOGRIFY, CARRY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            listSelection = arguments?.getStringArrayList(Companion.selectionTag) ?: arrayListOf()
            tintColor = arguments?.getInt(Companion.colorTag) ?: tintColor
            enumSelection = arguments?.getParcelable(Companion.enumTag) ?: Selections.NONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.dialog_selection, container, false)
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, tintColor))
        with(view.dialog_selection_recycler_view_container) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SelectionAdapter(listSelection)
            val dividerDecorator = DividerItemDecoration(context, RecyclerView.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.divider)?.let { dividerDecorator.setDrawable(it) }
            addItemDecoration(dividerDecorator)
        }

        view.dialog_selection_button_cancel.setOnClickListener {
            this.dismiss()
        }
        view.dialog_selection_button_confirm.setOnClickListener {
            val adapter = view.dialog_selection_recycler_view_container.adapter as SelectionAdapter
            when (enumSelection) {
                Selections.TAKE -> listener?.onSelectionDialogInteractionTake(adapter.getSelectedItem())
                Selections.DROP -> listener?.onSelectionDialogInteractionDrop(adapter.getSelectedItem())
                Selections.BUY -> listener?.onSelectionDialogInteractionBuy(adapter.getSelectedItem())
                Selections.SELL -> listener?.onSelectionDialogInteractionSell(adapter.getSelectedItem())
                Selections.WEAR -> listener?.onSelectionDialogInteractionWear(adapter.getSelectedItem())
                Selections.UNDRESS -> listener?.onSelectionDialogInteractionUndress(adapter.getSelectedItem())
                Selections.EXAMINE -> listener?.onSelectionDialogInteractionExamine(adapter.getSelectedItem())
                Selections.DASH -> listener?.onSelectionDialogInteractionDash(adapter.getSelectedItem())
                Selections.TRANSMOGRIFY -> listener?.onSelectionDialogInteractionTransmogrify(adapter.getSelectedItem())
                Selections.CARRY -> listener?.onSelectionDialogInteractionCarry(adapter.getSelectedItem())
                else -> UserInteraction.inform(this.context ?: requireContext(), "Problem showing selection dialog...")
            }
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnSelectionDialogInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnSelectionDialogInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnSelectionDialogInteractionListener {
        fun onSelectionDialogInteractionTake(item: String)
        fun onSelectionDialogInteractionDrop(item: String)
        fun onSelectionDialogInteractionBuy(item: String)
        fun onSelectionDialogInteractionSell(item: String)
        fun onSelectionDialogInteractionWear(item: String)
        fun onSelectionDialogInteractionUndress(item: String)
        fun onSelectionDialogInteractionExamine(item: String)
        fun onSelectionDialogInteractionDash(item: String)
        fun onSelectionDialogInteractionTransmogrify(item: String)
        fun onSelectionDialogInteractionCarry(item: String)
    }

}
