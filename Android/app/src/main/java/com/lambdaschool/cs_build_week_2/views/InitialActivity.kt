package com.lambdaschool.cs_build_week_2.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.utils.SharedPrefs
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.synthetic.main.activity_initial.*

class InitialActivity : AppCompatActivity(), InputDialog.OnInputDialogInteractionListener {

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
        val inputDialog=InputDialog()
        val bundle=Bundle()
        bundle.putString(InputDialog.textTag,"sample")
        bundle.putInt(InputDialog.colorTag,R.color.colorCupid)
        bundle.putParcelable(InputDialog.enumTag,InputDialog.Inputs.CHANGE_NAME)
        inputDialog.arguments=bundle
        inputDialog.isCancelable=false
        inputDialog.show(supportFragmentManager,InputDialog.textTag)
    }

    override fun onInputDialogInteractionChangeName(text: String) {
        return
    }
}
