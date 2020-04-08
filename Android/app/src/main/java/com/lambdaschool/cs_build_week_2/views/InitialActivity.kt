package com.lambdaschool.cs_build_week_2.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.utils.SharedPrefs
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.synthetic.main.activity_initial.*

class InitialActivity : AppCompatActivity(), SelectionDialog.OnSelectionDialogInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_initial)
        title = "Lambda Treasure Hunt - Token"
        edit_token.showSoftInputOnFocus = false
        button_begin.setOnClickListener {
            if (edit_token.text.length == 40) {
                MainActivity.authorizationToken = edit_token.text.toString()
                SharedPrefs.loadState()
                finish()
                overridePendingTransition(0, android.R.anim.fade_out)
            } else {
                UserInteraction.inform(this, "Invalid token. Try again...")
            }
        }

        val selectionDialog: SelectionDialog = SelectionDialog()
        val listBundle = Bundle()
        listBundle.putStringArrayList(
            SelectionDialog.selectionTag,
            arrayListOf("boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk")
        )
        listBundle.putInt(SelectionDialog.colorTag,R.color.colorForest)
        listBundle.putParcelable(SelectionDialog.enumTag, SelectionDialog.Selections.TAKE)
        selectionDialog.arguments = listBundle
        selectionDialog.isCancelable = false
        selectionDialog.show(supportFragmentManager, SelectionDialog.selectionTag)
    }

    override fun onSelectionDialogInteraction(item: String) {
        return
    }
}
