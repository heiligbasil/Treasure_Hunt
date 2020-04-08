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
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.fragment_selection.view.*
import java.util.*


class SelectionFragment : DialogFragment() {

    companion object {
        const val selectionTag = "item_list"
        const val colorTag = "tint_color"
        const val enumTag = "enum_selection"
    }
    private var listener: OnListFragmentInteractionListener? = null
    private var listSelection: ArrayList<String> = arrayListOf()
    private var tintColor: Int = R.color.colorAmber
    private var enumSelection: Selections = Selections.NONE

    @Parcelize
    enum class Selections : Parcelable {
        NONE, TAKE, DROP, BUY, SELL, WEAR, UNDRESS, EXAMINE, CHANGE_NAME, DASH, TRANSMOGRIFY, CARRY
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
        val view: View = inflater.inflate(R.layout.fragment_selection, container, false)
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, tintColor))
        with(view.selection_fragment_recycler_view_container) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SelectionAdapter(listSelection)
            val dividerDecorator = DividerItemDecoration(context, RecyclerView.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.divider)?.let { dividerDecorator.setDrawable(it) }
            addItemDecoration(dividerDecorator)
        }

        view.selection_fragment_button_cancel.setOnClickListener {
            this.dismiss()
        }
        view.selection_fragment_button_confirm.setOnClickListener {
            val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>? = view.selection_fragment_recycler_view_container.adapter
            listener?.onListFragmentInteraction((adapter as SelectionAdapter).getSelectedItem())
        }

        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnListFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: String)
    }

}
