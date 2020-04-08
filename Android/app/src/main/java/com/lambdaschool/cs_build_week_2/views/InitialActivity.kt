package com.lambdaschool.cs_build_week_2.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.utils.SharedPrefs
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.synthetic.main.activity_initial.*

class InitialActivity : AppCompatActivity(), SelectionFragment.OnListFragmentInteractionListener {

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

        val selectionFragment: SelectionFragment = SelectionFragment()
        val listBundle = Bundle()
        listBundle.putStringArrayList(
            SelectionFragment.selectionTag,
            arrayListOf("boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk","boots", "jacket", "treasure", "snitch", "coin", "map", "junk")
        )
        listBundle.putInt(SelectionFragment.colorTag,R.color.colorForest)
        listBundle.putParcelable(SelectionFragment.enumTag, SelectionFragment.Selections.TAKE)
        selectionFragment.arguments = listBundle
        selectionFragment.isCancelable = false
        selectionFragment.show(supportFragmentManager, SelectionFragment.selectionTag)
    }

    override fun onListFragmentInteraction(item: String) {
        return
    }
}
