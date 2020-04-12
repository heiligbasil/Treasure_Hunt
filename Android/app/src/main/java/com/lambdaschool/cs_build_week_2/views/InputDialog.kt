package com.lambdaschool.cs_build_week_2.views

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.dialog_input.view.*


class InputDialog : DialogFragment() {

    companion object {
        const val textTag = "text_input"
        const val colorTag = "color_input"
        const val enumTag = "enum_input"
    }

    private var listener: OnInputDialogInteractionListener? = null
    private var textInput: String = ""
    private var colorInput: Int = R.color.colorAmber
    private var enumInput: Inputs = Inputs.NONE

    @Parcelize
    enum class Inputs : Parcelable {
        NONE, CHANGE_NAME, EXAMINE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            textInput = arguments?.getString(textTag) ?: ""
            colorInput = arguments?.getInt(colorTag) ?: colorInput
            enumInput = arguments?.getParcelable(enumTag) ?: Inputs.NONE
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.dialog_input, container, false)
        view.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(view.context, colorInput))
        view.dialog_input_edit_text.addTextChangedListener {
            if (it.isNullOrEmpty()) {
                view.dialog_input_button_confirm.isEnabled = false
                view.dialog_input_button_confirm.backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.colorForestFade)
            } else {
                view.dialog_input_button_confirm.isEnabled = true
                view.dialog_input_button_confirm.backgroundTintList = ContextCompat.getColorStateList(view.context, R.color.colorForest)
            }
        }
        view.dialog_input_edit_text.setText(textInput)
        view.dialog_input_edit_text.showSoftInputOnFocus = false
        view.dialog_input_button_cancel.setOnClickListener {
            this.dismiss()
        }
        view.dialog_input_button_confirm.setOnClickListener {
            val text: String = view.dialog_input_edit_text.text.toString()
            when (enumInput) {
                Inputs.CHANGE_NAME -> listener?.onInputDialogInteractionChangeName(text)
                Inputs.EXAMINE -> listener?.onInputDialogInteractionExamine(text)
                else -> UserInteraction.inform(this.context ?: requireContext(), "Problem showing input dialog...")
            }
            this.dismiss()
        }
        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnInputDialogInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnInputDialogInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnInputDialogInteractionListener {
        fun onInputDialogInteractionChangeName(text: String)
        fun onInputDialogInteractionExamine(text: String)
    }

}
