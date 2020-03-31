package com.lambdaschool.cs_build_week_2.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.api.InitInterface
import com.lambdaschool.cs_build_week_2.api.MoveInterface
import com.lambdaschool.cs_build_week_2.api.StatusInterface
import com.lambdaschool.cs_build_week_2.api.TakeInterface
import com.lambdaschool.cs_build_week_2.models.*
import com.lambdaschool.cs_build_week_2.utils.SharedPrefs
import com.lambdaschool.cs_build_week_2.utils.UserInteraction
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.internal.toHexString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    companion object {
        var currentRoomId: Int = 0
        val roomDetails = RoomDetails()
        val cardinalReference: HashMap<String, String> = hashMapOf(Pair("n", "s"), Pair("s", "n"), Pair("e", "w"), Pair("w", "e"))
        val roomConnections: HashMap<String, Int?> = hashMapOf(Pair("n", null), Pair("s", null), Pair("e", null), Pair("w", null))
        val cellDetails = CellDetails()
        val roomsGraph = HashMap<Int?, ArrayList<Any?>>()
        var authorizationToken: String? = null
        lateinit var preferences: SharedPreferences
        fun initialize(context: Context) {
            if (!Companion::preferences.isInitialized) {
                preferences = context.getSharedPreferences("RoomsData", Context.MODE_PRIVATE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Lambda Treasure Hunt"
        initialize(this)
        startActivity(Intent(this, InitialActivity::class.java))

        button_move_north.setOnClickListener { moveInDirection("n") }
        button_move_south.setOnClickListener { moveInDirection("s") }
        button_move_east.setOnClickListener { moveInDirection("e") }
        button_move_west.setOnClickListener { moveInDirection("w") }
        button_init.setOnClickListener { networkGetInit() }
        button_take.setOnClickListener {
            //TODO: Initialize data properly before GET Init is run...and maybe disable all buttons until it is
            val roomItems = (roomsGraph[currentRoomId]?.get(0) as RoomDetails).items as ArrayList<String>
            if (roomItems.isNotEmpty()) {
                val takeTreasure: TakeTreasure = TakeTreasure(roomItems[0])
                networkPostTake(takeTreasure)
            }
            else {
                UserInteraction.inform(this,"Nothing to take!")
            }
        }
        button_drop.setOnClickListener {}
        button_status.setOnClickListener { networkPostStatus() }
        button_buy.setOnClickListener {}
        button_sell.setOnClickListener {
            UserInteraction.askQuestion(
                this,
                "Shopkeeper Sunnie",
                "Are you sure you want to sell that item?",
                "Confirm",
                "Just browsing"
            )
        }
        button_wear.setOnClickListener {}
        button_undress.setOnClickListener {}
        button_examine.setOnClickListener {}
        button_change_name.setOnClickListener {}
        button_pray.setOnClickListener {}
        button_fly.setOnClickListener {}
        button_dash.setOnClickListener {}
        button_player_state.setOnClickListener {}
        button_transmogrify.setOnClickListener {}
        button_carry.setOnClickListener {}
        button_receive.setOnClickListener {}
        button_warp.setOnClickListener {}
        button_recall.setOnClickListener {}
        button_mine.setOnClickListener {}
        button_totals.setOnClickListener {}
        button_last_proof.setOnClickListener {}
        button_get_balance.setOnClickListener {}


    }

    private fun moveInDirection(direction: String) {
        val nextRoom: String? = anticipateNextRoom(direction)
        val moveWisely: MoveWisely = MoveWisely(direction, nextRoom)
        networkPostMove(moveWisely)
    }

    private fun networkGetInit() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: InitInterface = retrofit.create(InitInterface::class.java)
        val call: Call<RoomDetails> = service.getRoomInit()
        call.enqueue(object : Callback<RoomDetails> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
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
                    updateGraphDetails(responseBody)
                    SharedPrefs.saveState()
                    text_room_info.text = responseBody.toString()
                    text_log.append("Code ${response.code()}: Init success!\n")
                    UserInteraction.inform(applicationContext, "${response.code()}: Init success!")
                } else {
                    val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()
                        ?.substringBefore("Django Version:")}"
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
            }
        })
    }

    private fun networkPostMove(moveWisely: MoveWisely) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: MoveInterface = retrofit.create(MoveInterface::class.java)
        val call: Call<RoomDetails> = service.postMove(moveWisely)
        call.enqueue(object : Callback<RoomDetails> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
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
                    val originalRoomId: Int = currentRoomId
                    val responseBody: RoomDetails? = response.body()
                    updateGraphDetails(responseBody)
                    setRoomIdForPreviousRoom(cardinalReference[moveWisely.direction], originalRoomId)
                    SharedPrefs.saveState()
                    text_room_info.text = responseBody.toString()
                    val message: String = "Code ${response.code()}: Move success!\n${responseBody?.messages?.joinToString("\n")}"
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()
                        ?.substringBefore("Django Version:")}"
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
            }
        })
    }

    private fun networkPostStatus() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: StatusInterface = retrofit.create(StatusInterface::class.java)
        val call: Call<Status> = service.postStatus()
        call.enqueue(object : Callback<Status> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<Status>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            /**
             * Invoked for a received HTTP response.
             *
             *
             * Note: An HTTP response may still indicate an application-level failure such as a 404 or 500.
             * Call [Response.isSuccessful] to determine if the response indicates success.
             */
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    val responseBody: Status? = response.body()
                    text_room_info.text = responseBody.toString()
                    val message: String =
                        "Code ${response.code()}: Inventory status success!\n${responseBody?.messages?.joinToString("\n")}"
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()
                        ?.substringBefore("Django Version:")}"
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
            }
        })
    }

    private fun networkPostTake(takeTreasure: TakeTreasure) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: TakeInterface = retrofit.create(TakeInterface::class.java)
        val call: Call<RoomDetails> = service.postTake(takeTreasure)
        call.enqueue(object : Callback<RoomDetails> {
            /**
             * Invoked when a network exception occurred talking to the server or when an unexpected
             * exception occurred creating the request or processing the response.
             */
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
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
                    text_room_info.text = responseBody.toString()
                    val message: String = "Code ${response.code()}: Take success!\n${responseBody?.messages?.joinToString("\n")}"
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorText = "${response.message()} ${response.code()}: ${response.errorBody()?.string()
                        ?.substringBefore("Django Version:")}"
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
            }
        })
    }

    private fun updateGraphDetails(responseBody: RoomDetails?) {
        currentRoomId = responseBody?.roomId ?: 0
        if (roomsGraph[currentRoomId].isNullOrEmpty())
            roomsGraph[currentRoomId] = arrayListOf<Any?>(roomDetails, roomConnections, cellDetails)
        roomsGraph[currentRoomId]?.set(0, responseBody)
        roomsGraph[currentRoomId]?.set(1, validateRoomConnections(currentRoomId))
        roomsGraph[currentRoomId]?.set(2, fillCellDetails(currentRoomId))
    }

    private fun anticipateNextRoom(direction: String): String? {
        val directionAssociations = roomsGraph[currentRoomId]?.get(1) as HashMap<*, *>
        return (directionAssociations[direction] as Int?)?.toString()
    }

    private fun setRoomIdForPreviousRoom(direction: String?, roomId: Int?) {
        @Suppress("UNCHECKED_CAST")
        (roomsGraph[currentRoomId]!![1] as HashMap<String, Int?>)[direction ?: "n"] = roomId
    }

    private fun validateRoomConnections(roomId: Int?): HashMap<String, Int?> {
        val extractedRoomDetailsExits = (roomsGraph[roomId]?.get(0) as RoomDetails).exits
        val extractedRoomConnections = roomsGraph[roomId]?.get(1) as HashMap<*, *>
        val validExits: HashMap<String, Int?> = hashMapOf()
        extractedRoomDetailsExits?.forEach {
            validExits[it] = extractedRoomConnections[it] as Int?
        }
        return validExits
    }

    private fun fillCellDetails(roomId: Int?): CellDetails {
        val extractedRoomDetails: RoomDetails = roomsGraph[roomId]?.get(0) as RoomDetails
        val coordinates: String = extractedRoomDetails.coordinates ?: "(0,0)"
        val coordinatesSplit: List<String> = coordinates.substring(1, coordinates.length - 1).split(",")
        // TODO: Change cell color depending on condition
        return CellDetails(coordinatesSplit[0].toInt(), coordinatesSplit[1].toInt(), Color.BLUE.toHexString())
    }
}
