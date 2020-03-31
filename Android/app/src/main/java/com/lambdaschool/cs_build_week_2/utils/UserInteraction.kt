package com.lambdaschool.cs_build_week_2.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object UserInteraction {
    fun askQuestion(context: Context, title: String, message: String, positiveButton: String, negativeButton: String): Boolean {
        var result: Boolean = false
        AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert)
            .setTitle(title)
            .setCancelable(false)
            .setIcon(android.R.drawable.ic_dialog_dialer)
            .setMessage(message)
            .setPositiveButton(positiveButton) { dialog, _ ->
                dialog.cancel()
                result = true
            }
            .setNegativeButton(negativeButton) { dialog, _ ->
                dialog.cancel()
                result = false
            }
            .show()
        return result
    }
}