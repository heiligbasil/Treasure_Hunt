package com.lambdaschool.cs_build_week_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit=Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val service=retrofit.create(Interface::class.java)

    }
}
