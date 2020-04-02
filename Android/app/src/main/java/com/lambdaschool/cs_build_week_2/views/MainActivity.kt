package com.lambdaschool.cs_build_week_2.views

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.api.*
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
import java.io.IOException
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    companion object {
        val traversalTimer = Timer()
        val timerHandler = Handler()
        var cooldownTimer: CountDownTimer? = null
        var cooldownAmount: Double? = 0.0
        var automatedPath: ArrayList<Int?> = arrayListOf()
        var currentRoomId: Int = -1
        var responseMessage: String = ""
        var responseRoomInfo: String = ""
        var inventoryStatus: Status = Status()
        val roomDetails = RoomDetails()
        val cardinalReference: HashMap<String, String> = hashMapOf(Pair("n", "s"), Pair("s", "n"), Pair("e", "w"), Pair("w", "e"))
        val roomConnections: HashMap<String, Int?> = hashMapOf(Pair("n", null), Pair("s", null), Pair("e", null), Pair("w", null))
        val cellDetails = CellDetails()
        val roomsGraph = HashMap<Int?, ArrayList<Any?>>()
        var authorizationToken: String? = null
        lateinit var preferences: SharedPreferences
        fun initializeCompanion(context: Context) {
            if (!Companion::preferences.isInitialized) {
                preferences = context.getSharedPreferences("RoomsData", Context.MODE_PRIVATE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Lambda Treasure Hunt"
        initializeCompanion(this)
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
                val treasure: Treasure = Treasure(roomItems[0])
                networkPostTakeTreasure(treasure)
            } else {
                UserInteraction.inform(this, "Nothing to take!")
            }
        }
        button_drop.setOnClickListener {
            moveToSpecificRoomAutomated(63)
        }
        button_status.setOnClickListener { networkPostStatus() }
        button_buy.setOnClickListener {
            //TODO: Initialize data properly before GET Init is run...and maybe disable all buttons until it is
            val treasure: Treasure = Treasure("donuts")
            networkPostBuyTreasure(treasure)
        }
        button_sell.setOnClickListener {
            //TODO: Initialize data properly before GET Init is run...and maybe disable all buttons until it is
            val roomItems: List<String> = inventoryStatus.inventory ?: listOf()
            if (roomItems.isNotEmpty()) {
                val randomItem: String = roomItems.random()
                val treasure: Treasure = Treasure(randomItem, "yes")
                networkPostSellTreasure(treasure)
//                if( UserInteraction.askQuestion(
//                    this,
//                    "Mechanical Shopkeeper",
//                    "Are you sure you want to sell '$randomItem'?",
//                    "Confirm",
//                    "Just browsing"
//                )){
//                    treasure = Treasure(randomItem, "yes")
//                    networkPostSellTreasure(treasure)
//                }
            } else {
                UserInteraction.inform(this, "Nothing to sell!")
            }
        }
        button_wear.setOnClickListener {}
        button_undress.setOnClickListener {}
        button_examine.setOnClickListener {
            //TODO: Initialize data properly before GET Init is run...and maybe disable all buttons until it is
            val roomItems = (roomsGraph[currentRoomId]?.get(0) as RoomDetails).items as ArrayList<String>
            val players = (roomsGraph[currentRoomId]?.get(0) as RoomDetails).players as ArrayList<String>
            val inventoryItems= inventoryStatus.inventory  ?: arrayListOf()
            val combined = roomItems+players+inventoryItems
            if (combined.isNotEmpty()) {
                val treasure: Treasure = Treasure(combined.random())
                networkPostExamineTreasure(treasure)
            } else {
                UserInteraction.inform(this, "Nothing to Examine!")
            }
        }
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
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    updateGraphDetails(responseBody)
                    SharedPrefs.saveState()
                    text_room_info.text = responseBody.toString()
                    text_log.append("Code ${response.code()}: Init success!\n")
                    UserInteraction.inform(applicationContext, "${response.code()}: Init success!")
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
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

        var executeResponse: Response<RoomDetails>? = null
        try {
            executeResponse = call.execute()
        } catch (ioe: IOException) {
            responseMessage = "IOException: ${ioe.message}\n"
        } catch (rte: RuntimeException) {
            responseMessage = "RuntimeException: ${rte.message}\n"
        }

        if (executeResponse?.isSuccessful == true) {
            val originalRoomId: Int = currentRoomId
            val responseAsRoomDetails: RoomDetails = executeResponse.body() as RoomDetails
            responseRoomInfo = responseAsRoomDetails.toString()
            cooldownAmount = responseAsRoomDetails.cooldown
            responseMessage = "Code ${executeResponse.code()}: "
            if (responseAsRoomDetails.errors?.isNotEmpty() == true) {
                responseMessage += "Move failure (\"${moveWisely.direction}\")\n${responseAsRoomDetails.errors?.joinToString("\n")}"
            } else {
                responseMessage += "Move success!\n${responseAsRoomDetails.messages?.joinToString("\n")}"
                updateGraphDetails(responseAsRoomDetails)
                setRoomIdForPreviousRoom(cardinalReference[moveWisely.direction], originalRoomId)
                SharedPrefs.saveState()
//                view_map.calculateSize()
            }
        } else {
            val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
            val errorBody: ErrorBody = Gson().fromJson(executeResponse?.errorBody()?.string(), errorBodyTypeCast)
            responseMessage = "${executeResponse?.message()} ${executeResponse?.code()}:\n$errorBody"
            cooldownAmount = errorBody.cooldown
        }
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
            override fun onFailure(call: Call<Status>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    val responseBody: Status = response.body() as Status
                    inventoryStatus = responseBody
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    val message: String =
                        "Code ${response.code()}: Inventory status success!\n${responseBody.messages?.joinToString("\n")}"
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostBuyTreasure(treasure: Treasure) {
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
        val service: BuyInterface = retrofit.create(BuyInterface::class.java)
        val call: Call<RoomDetails> = service.postBuy(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Buy failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Buy success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostSellTreasure(treasure: Treasure) {
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
        val service: SellInterface = retrofit.create(SellInterface::class.java)
        val call: Call<RoomDetails> = service.postSell(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Sell failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Sell success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostExamineTreasure(treasure: Treasure) {
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
        val service: ExamineInterface = retrofit.create(ExamineInterface::class.java)
        val call: Call<RoomDetails> = service.postExamine(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Examine failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Examine success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostTakeTreasure(treasure: Treasure) {
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
        val call: Call<RoomDetails> = service.postTake(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Take failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Take success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun moveInDirection(direction: String) {
        if (roomsGraph.isNotEmpty() && currentRoomId != -1) {
            val nextRoom: String? = anticipateNextRoom(direction)
            val moveWisely: MoveWisely = MoveWisely(direction, nextRoom)
            val networkRunnable: Runnable = Runnable {
                networkPostMove(moveWisely)
            }
            val networkThread = Thread(networkRunnable)
            networkThread.start()
            networkThread.join()
            text_room_info.text = responseRoomInfo
            text_log.append(responseMessage)
            UserInteraction.inform(applicationContext, responseMessage)
            showCooldownTimer()
        } else {
            UserInteraction.inform(this, "Please do a GET Init first...")
        }
    }

    private fun updateGraphDetails(responseBody: RoomDetails?) {
        currentRoomId = responseBody?.roomId ?: 0
        if (roomsGraph[currentRoomId].isNullOrEmpty())
            roomsGraph[currentRoomId] = arrayListOf<Any?>(roomDetails, roomConnections, cellDetails)
        roomsGraph[currentRoomId]?.set(0, responseBody)
        roomsGraph[currentRoomId]?.set(1, validateRoomConnections(currentRoomId))
        roomsGraph[currentRoomId]?.set(2, fillCellDetails(currentRoomId))
        view_map.calculateSize()
    }

    private fun anticipateNextRoom(direction: String): String? {
        val directionAssociations = roomsGraph[currentRoomId]?.get(1) as HashMap<*, *>
        return (directionAssociations[direction] as Int?)?.toString()
    }

    private fun setRoomIdForPreviousRoom(direction: String?, roomId: Int?) {
        if (currentRoomId != roomId) {
            @Suppress("UNCHECKED_CAST")
            (roomsGraph[currentRoomId]!![1] as HashMap<String, Int?>)[direction ?: "n"] = roomId
        }
    }

    private fun getDirectionsFromRoom(roomId: Int?): HashMap<String, Int?> {
        if (roomId == null)
            return hashMapOf()
        val roomTrifecta: ArrayList<Any?> = roomsGraph[roomId] ?: arrayListOf()
        @Suppress("UNCHECKED_CAST")
        return roomTrifecta[1] as HashMap<String, Int?>
    }

    private fun getDirectionForRoom(roomId: Int?): String? {
        val roomDirections: HashMap<String, Int?> = getDirectionsFromRoom(currentRoomId)
        val directions: Set<String> = roomDirections.filterValues {
            it == roomId
        }.keys
        return if (directions.isNotEmpty())
            directions.first()
        else
            null
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
        return CellDetails(coordinatesSplit[0].toInt(), coordinatesSplit[1].toInt(), "#${Color.BLUE.toHexString()}")
    }

    private fun showCooldownTimer() {
        cooldownTimer?.cancel()
        frame_cooldown.visibility = View.VISIBLE
        cooldownTimer = object : CountDownTimer((cooldownAmount?.times(1000))?.toLong() ?: 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val timerText = "CD: " + "${(millisUntilFinished / 1000)}".padStart(2, '0')
                text_cooldown.text = timerText
            }

            override fun onFinish() {
                frame_cooldown.visibility = View.INVISIBLE
                cooldownAmount = 0.0
            }
        }
        cooldownTimer?.start()
    }

    private fun runNextAutomatedStep() {
        timerHandler.post(myRunnable)
    }

    @OptIn(ExperimentalStdlibApi::class)
    val myRunnable = Runnable {
        val destinationRoom: Int? = automatedPath.last()
        val direction: String? = getDirectionForRoom(automatedPath.removeFirst())
        if (direction == null) {
            UserInteraction.askQuestion(
                this, "Room Not Found", "Problem encountered traversing to room #$destinationRoom!", "Okay", null
            )
        } else {
            when (direction) {
                "n" -> button_move_north.performClick()
                "s" -> button_move_south.performClick()
                "e" -> button_move_east.performClick()
                "w" -> button_move_west.performClick()
            }
        }
        if (automatedPath.isEmpty()) {
            traversalTimer.cancel()
        }
        if (destinationRoom == currentRoomId) {
            UserInteraction.askQuestion(this, "Room Found", "Room #$destinationRoom has been found!", "Okay", null)
        }
    }

    private fun moveToSpecificRoomAutomated(roomId: Int) {
        automatedPath = bfs(roomId)
        traversalTimer.schedule(object : TimerTask() {
            override fun run() {
                runNextAutomatedStep()
            }
        }, 0, 16000)

        /*val pathToRoom: ArrayList<Int?> = bfs(roomId)
        pathToRoom.forEach {
            val direction: String? = getDirectionForRoom(it)
            if (direction == null) {
                UserInteraction.askQuestion(this, "Room Not Found", "Problem encountered traversing to room #$roomId!", "Okay", null)
            } else {
                when (direction) {
                    "n" -> button_move_north.performClick()
                    "s" -> button_move_south.performClick()
                    "e" -> button_move_east.performClick()
                    "w" -> button_move_west.performClick()
                }
                //moveInDirection(direction)
                Thread.sleep(cooldownAmount?.times(1000)?.toLong() ?: 1000)
            }
        }
        if (roomId == currentRoomId) {
            UserInteraction.askQuestion(this, "Room Found", "Room #$roomId has been found!", "Okay", null)
        }*/
    }

    private fun bfs(destinationRoom: Int): ArrayList<Int?> {
        val queue: Queue<ArrayList<Int?>> = LinkedList()
        queue.add(arrayListOf(currentRoomId))
        val contemplated: MutableSet<Int?> = mutableSetOf()
        while (queue.isNotEmpty()) {
            val path: ArrayList<Int?> = queue.remove()
            val room: Int? = path.last()
            if (!contemplated.contains(room)) {
                contemplated.add(room)
                if (room == destinationRoom) {
                    return ArrayList(path.subList(1, path.size))
                }
                getDirectionsFromRoom(room).values.forEach {
                    val pathCopy: ArrayList<Int?> = path.toCollection(arrayListOf())
                    pathCopy.add(it)
                    queue.add(pathCopy)
                }
            }
        }
        return arrayListOf()
    }
}
