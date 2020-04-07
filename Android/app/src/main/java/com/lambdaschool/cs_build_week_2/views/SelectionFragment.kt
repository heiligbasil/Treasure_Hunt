package com.lambdaschool.cs_build_week_2.views

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
import kotlinx.android.synthetic.main.fragment_selection.view.*
import java.util.*


class SelectionFragment : DialogFragment() {

    private var listener: OnListFragmentInteractionListener? = null
    private var listBundle: ArrayList<String> = arrayListOf()
    val selectionTag = "item_list"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            listBundle = arguments?.getStringArrayList(selectionTag) ?: arrayListOf()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_selection, container, false)
        /*view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, R.color.colorAmber))*/
        with(view.selection_fragment_recycler_view_container) {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SelectionAdapter(listBundle, listener)
            val dividerDecorator = DividerItemDecoration(context, RecyclerView.VERTICAL)
            ContextCompat.getDrawable(context, R.drawable.divider)?.let { dividerDecorator.setDrawable(it) }
            addItemDecoration(dividerDecorator)
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
