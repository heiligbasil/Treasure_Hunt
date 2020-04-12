package com.lambdaschool.cs_build_week_2.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

object UserInteraction {
    fun askQuestion(
        context: Context,
        title: String,
        message: String,
        positiveButton: String,
        negativeButton: String?,
        listener: OnPositiveButtonListener? = null
    ) {
        AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle(title)
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_dialer)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.cancel()
                listener?.positiveButtonClickedCallback()
            }
            .setNegativeButton(negativeButton) { dialog, _ -> dialog.cancel() }
            .show()
    }

    interface OnPositiveButtonListener {
        fun positiveButtonClickedCallback()
    }

    fun inform(context: Context, message: String) {
        val toast: Toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }
}