package com.lambdaschool.cs_build_week_2

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.api.AdvInitInterface
import com.lambdaschool.cs_build_week_2.models.CellDetails
import com.lambdaschool.cs_build_week_2.models.RoomDetails
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

        token.setOnEditorActionListener { v, _, _ ->
            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request().newBuilder().addHeader("Authorization", "Token ${v.text}").build()
                    chain.proceed(request)
                }.build())
                .build()
            val service: AdvInitInterface = retrofit.create(AdvInitInterface::class.java)
            val call: Call<RoomDetails> = service.getRoomInit()
            call.enqueue(object : Callback<RoomDetails> {
                /**
                 * Invoked when a network exception occurred talking to the server or when an unexpected
                 * exception occurred creating the request or processing the response.
                 */
                override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
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
                override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                    if (response.isSuccessful) {
                        val responseBody: RoomDetails? = response.body()
                        val roomId: Int? = responseBody?.roomId
                        if (roomsGraph[roomId].isNullOrEmpty())
                            roomsGraph[roomId] = arrayOfRoomAndCellDetails
                        roomsGraph[roomId]?.set(0, responseBody)
                        roomsGraph[roomId]?.set(1, validateRoomConnections(roomId))
                        roomsGraph[roomId]?.set(2, fillCellDetails(roomId))
                    } else {
                        val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()
                            ?.substringBefore("Django Version:")}"
                        val toast: Toast = Toast.makeText(this@MainActivity, errorText, Toast.LENGTH_LONG)
                        toast.setGravity(Gravity.CENTER, 0, 0)
                        toast.show()
                    }
                }

            })
            return@setOnEditorActionListener true
        }

    }

    private fun validateRoomConnections(roomId: Int?): HashMap<String, Int?> {
        val extractedRoomDetailsExits = (roomsGraph[roomId]?.get(0) as RoomDetails).exits
        val extractedRoomConnections = roomsGraph[roomId]?.get(1) as HashMap<*, *>
        val validExits: HashMap<String, Int?> = hashMapOf()
        if (extractedRoomDetailsExits?.size != extractedRoomConnections.size) {
            extractedRoomDetailsExits?.forEach {
                validExits[it] = extractedRoomConnections[it] as Int?
            }
        }
        return validExits
    }

    private fun fillCellDetails(roomId: Int?): CellDetails {
        val extractedRoomDetails: RoomDetails = roomsGraph[roomId]?.get(0) as RoomDetails
        val coordinates: String = extractedRoomDetails.coordinates ?: "(0,0)"
        val coordinatesSplit: List<String> = coordinates.substring(1, coordinates.length - 1).split(",")
        // TODO: Change cell color depending on condition
        return CellDetails(coordinatesSplit[0].toInt(), coordinatesSplit[1].toInt(), Color.BLUE)
    }

    val roomDetails = RoomDetails()
    val roomConnections: HashMap<String, Int?> = hashMapOf(Pair("n", null), Pair("s", null), Pair("e", null), Pair("w", null))
    val cellDetails = CellDetails()
    val arrayOfRoomAndCellDetails: ArrayList<Any?> = arrayListOf(roomDetails, roomConnections, cellDetails)

    companion object {
        val roomsGraph = HashMap<Int?, ArrayList<Any?>>()
    }
}
