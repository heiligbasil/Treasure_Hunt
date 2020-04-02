package com.lambdaschool.cs_build_week_2.views

import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.utils.SharedPrefs
import kotlinx.android.synthetic.main.activity_initial.*

class InitialActivity : AppCompatActivity() {

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
                val toast: Toast = Toast.makeText(this, "Invalid token. Try again...", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
        }
    }
}
