package com.lambdaschool.cs_build_week_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.lambdaschool.cs_build_week_2.api.AdvInitInterface
import com.lambdaschool.cs_build_week_2.models.Room
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
//            .client(OkHttpClient.Builder().addInterceptor { chain ->
//                val request = chain.request().newBuilder().addHeader("Authorization", "Token ${token.text}").build()
//            }.build())
            .build()
        val service: AdvInitInterface = retrofit.create(AdvInitInterface::class.java)
        val call: Call<Room> = service.getRoomInit()
        call.enqueue(object : Callback<Room> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<Room>, t: Throwable) {
                val toast: Toast = Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<Room>, response: Response<Room>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    return
                } else {
                    val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()?.substringBefore("Django Version:")}"
                    val toast: Toast = Toast.makeText(this@MainActivity, errorText, Toast.LENGTH_LONG)
                    toast.setGravity(Gravity.CENTER, 0, 0)
                    toast.show()
                }
            }

        })

    }
}
