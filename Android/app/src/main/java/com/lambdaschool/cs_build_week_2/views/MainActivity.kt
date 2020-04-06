package com.lambdaschool.cs_build_week_2.views

import android.content.*
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.view.View
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.lambdaschool.cs_build_week_2.R
import com.lambdaschool.cs_build_week_2.api.*
import com.lambdaschool.cs_build_week_2.models.*
import com.lambdaschool.cs_build_week_2.utils.Mining
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
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    companion object {
        var traversalTimer = Timer()
        val timerHandlerSpecific = Handler()
        val timerHandlerUnexplored = Handler()
        var cooldownTimer: CountDownTimer? = null
        var cooldownAmount: Double? = 0.0
        var automatedPath: ArrayList<Int?> = arrayListOf()
        var currentRoomId: Int = -1
        var responseMessage: String = ""
        var responseRoomInfo: String = ""
        var inventoryStatus: Status = Status()
        var examineShort: ExamineShort = ExamineShort()
        var traverseToRoom: Int = 309
        var mine: Mine = Mine(-1)
        var proof: Proof = Proof()
        val roomDetails: RoomDetails = RoomDetails()
        val cardinalReference: HashMap<String, String> = hashMapOf(Pair("n", "s"), Pair("s", "n"), Pair("e", "w"), Pair("w", "e"))
        val roomConnections: HashMap<String, Int?> = hashMapOf(Pair("n", null), Pair("s", null), Pair("e", null), Pair("w", null))
        val cellDetails = CellDetails()
        val roomsGraph = HashMap<Int?, ArrayList<Any?>>()
        val darkGraph = HashMap<Int?, ArrayList<Any?>>()
        var inDarkWorld = false
        var authorizationToken: String? = null
        lateinit var preferences: SharedPreferences
        lateinit var preferencesDark: SharedPreferences
        fun initializeCompanion(context: Context) {
            if (!Companion::preferences.isInitialized) {
                preferences = context.getSharedPreferences("RoomsData", Context.MODE_PRIVATE)
            }
            if (!Companion::preferencesDark.isInitialized) {
                preferencesDark = context.getSharedPreferences("DarkData", Context.MODE_PRIVATE)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "Lambda Treasure Hunt"
        initializeCompanion(this)
        startActivity(Intent(this, InitialActivity::class.java))
        text_room_info.setOnLongClickListener {
            if (isInitDataDownloaded()) {
                val descriptionToCopy: String = if (getCurrentRoomDetails().title == "Wishing Well") {
                    examineShort.description ?: "Nothing copied!"
                } else if (mine.proof != -1) {
                    mine.proof.toString()
                } else {
                    return@setOnLongClickListener false
                }
                val clipboard: ClipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip: ClipData = ClipData.newPlainText("LTH Description", descriptionToCopy)
                clipboard.setPrimaryClip(clip)
                UserInteraction.inform(this, "Copied the description to the clipboard.")
            }
            return@setOnLongClickListener true
        }
        button_move_north.setOnClickListener { moveInDirection("n") }
        button_move_south.setOnClickListener { moveInDirection("s") }
        button_move_east.setOnClickListener { moveInDirection("e") }
        button_move_west.setOnClickListener { moveInDirection("w") }
        button_init.setOnClickListener { networkGetInit() }
        button_traverse.setOnClickListener {
            moveToUnexploredAutomated(pauseInSeconds = 10)
//            moveToSpecificRoomAutomated(traverseToRoom, pauseInSeconds = 8)
        }
        button_take.setOnClickListener {
            if (isInitDataDownloaded()) {
                val roomItems = (getCurrentRoomDetails()).items as ArrayList<String>
                if (roomItems.isNotEmpty()) {
                    val treasure: Treasure = Treasure(roomItems.first())
                    roomItems.removeAt(0)
                    networkPostTakeTreasure(treasure)
                } else {
                    UserInteraction.inform(this, "Nothing to take!")
                }
            }
        }
        button_drop.setOnClickListener {
            if (isStatusDataDownloaded()) {
                val inventoryItems: MutableList<String> = inventoryStatus.inventory?.toMutableList() ?: arrayListOf()
                if (inventoryItems.isNotEmpty()) {
                    val treasure: Treasure = Treasure(inventoryItems.first())
                    inventoryItems.removeAt(0)
                    inventoryStatus.inventory = inventoryItems
                    networkPostDrop(treasure)
                } else {
                    UserInteraction.inform(this, "Nothing to drop!")
                }
            }
        }
        button_status.setOnClickListener { networkPostStatus() }
        button_buy.setOnClickListener {
            if (isInitDataDownloaded()) {
                var whatToBuy: String = ""
                if (getCurrentRoomDetails().title == "Red Egg Pizza Parlor") {
                    whatToBuy = "Pizza"
                } else if (getCurrentRoomDetails().title == "JKMT Donuts") {
                    whatToBuy = "Donut"
                }
                networkPostBuyTreasure(Treasure(whatToBuy, "yes"))
            }
        }
        button_sell.setOnClickListener {
            if (isStatusDataDownloaded()) {
                val inventoryItems: MutableList<String> = inventoryStatus.inventory?.toMutableList() ?: arrayListOf()
                if (inventoryItems.isNotEmpty()) {
                    val treasure: Treasure = Treasure(inventoryItems.first(), "yes")
                    inventoryItems.removeAt(0)
                    inventoryStatus.inventory = inventoryItems
                    networkPostSellTreasure(treasure)
                    /*UserInteraction.askQuestion(
                        this,
                        "Mechanical Shopkeeper",
                        "Are you sure you want to sell '${treasure.name}'?",
                        "Confirm",
                        "Just browsing"
                    )*/
                } else {
                    UserInteraction.inform(this, "Nothing to sell!")
                }
            }
        }
        button_wear.setOnClickListener {
            if (isStatusDataDownloaded()) {
                val footwear: String = "nice boots"
                val bodywear: String = "nice jacket"
                if (inventoryStatus.inventory?.contains(footwear) == true) {
                    networkPostWear(Treasure(footwear))
                } else if (inventoryStatus.inventory?.contains(bodywear) == true) {
                    networkPostWear(Treasure(bodywear))
                } else {
                    UserInteraction.inform(this, "Nothing to wear!")
                }
            }
        }
        button_undress.setOnClickListener {
            if (isStatusDataDownloaded()) {
                val footwear: String = "nice boots"
                val bodywear: String = "nice jacket"
                if (inventoryStatus.footwear?.toLowerCase(Locale.getDefault())?.contains(footwear) == true) {
                    networkPostUndress(Treasure(footwear))
                } else if (inventoryStatus.bodywear?.toLowerCase(Locale.getDefault())?.contains(bodywear) == true) {
                    networkPostUndress(Treasure(bodywear))
                } else {
                    UserInteraction.inform(this, "Nothing to undress!")
                }
            }
        }
        button_examine.setOnClickListener {
            if (isInitDataDownloaded()) {
                val currentRoomDetails: RoomDetails = getCurrentRoomDetails()
                if (currentRoomDetails.title == "Wishing Well") {
                    networkPostExamineShort(Treasure("Well"))
                } else if (currentRoomDetails.title == "Arron's Athenaeum") {
                    networkPostExamineShort(Treasure("Book"))
                } else {
                    val roomItems: java.util.ArrayList<String> = (currentRoomDetails).items as ArrayList<String>
                    val players: java.util.ArrayList<String> = (currentRoomDetails).players as ArrayList<String>
                    val inventoryItems: List<String> = inventoryStatus.inventory ?: arrayListOf()
                    val combined: List<String> = roomItems + players + inventoryItems
                    if (combined.isNotEmpty()) {
                        val treasure: Treasure = Treasure(combined.random())
                        networkPostExamineTreasure(treasure)
                    } else {
                        UserInteraction.inform(this, "Nothing to Examine!")
                    }
                }
            }
        }
        button_change_name.setOnClickListener { networkPostChangeName(Treasure("Basil der Grosse", "aye")) }
        button_pray.setOnClickListener {
            networkPostPray()
        }
        button_dash.setOnClickListener {
            val directionAndPath: HashMap<String, ArrayList<Int?>> =
                hashMapOf(Pair("n", arrayListOf()), Pair("s", arrayListOf()), Pair("e", arrayListOf()), Pair("w", arrayListOf()))
            directionAndPath.forEach { eachDirection ->
                var roomId: Int? = currentRoomId
                do {
                    val directionsFromRoom: HashMap<String, Int?> = getDirectionsFromRoom(roomId)
                    roomId = directionsFromRoom[eachDirection.key]
                    if (roomId != null)
                        directionAndPath[eachDirection.key]?.add(roomId)
                } while (roomId != null)
            }
            val dash: Dash = Dash("", "0", "")
            val easierListToProcess: HashMap<String, String> = hashMapOf()
            directionAndPath.forEach {
                easierListToProcess[it.key] = it.value.joinToString(separator = ",")
                if (it.value.size > dash.num_rooms.toInt()) {
                    dash.direction = it.key
                    dash.num_rooms = it.value.size.toString()
                    dash.next_room_ids = it.value.joinToString(separator = ",")
                }
            }
            networkPostDash(dash)
        }
        button_transmogrify.setOnClickListener { networkPostTransmogrify(Treasure("Small Treasure")) }
        button_carry.setOnClickListener { networkPostCarry(Treasure("Tiny Treasure")) }
        button_receive.setOnClickListener { networkPostReceive() }
        button_warp.setOnClickListener { networkPostWarp() }
        button_recall.setOnClickListener { networkPostRecall() }
        button_mine.setOnClickListener {
            if (isProofDataDownloaded()) {
                val mine: Mine = Mine(Mining.proofOfWork(proof.proof, proof.difficulty))
                networkPostMine(mine)
            }
        }
        button_last_proof.setOnClickListener { networkGetLastProof() }
        button_get_balance.setOnClickListener { networkGetBalance() }
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, "${response.code()}: Init success!")
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
            }
        } else {
            val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
            val errorBody: ErrorBody = Gson().fromJson(executeResponse?.errorBody()?.string(), errorBodyTypeCast)
            responseMessage = "${executeResponse?.message()} ${executeResponse?.code()}:\n$errorBody"
            cooldownAmount = errorBody.cooldown
        }
    }

    private fun networkPostFly(moveWisely: MoveWisely) {
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
        val service: FlyInterface = retrofit.create(FlyInterface::class.java)
        val call: Call<RoomDetails> = service.postFly(moveWisely)
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
                responseMessage += "Fly failure (\"${moveWisely.direction}\")\n${responseAsRoomDetails.errors?.joinToString("\n")}"
            } else {
                responseMessage += "Fly success!\n${responseAsRoomDetails.messages?.joinToString("\n")}"
                updateGraphDetails(responseAsRoomDetails)
                setRoomIdForPreviousRoom(cardinalReference[moveWisely.direction], originalRoomId)
                SharedPrefs.saveState()
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    val responseBody: Status = response.body() as Status
                    inventoryStatus = responseBody
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    val message: String =
                        "Code ${response.code()}: Inventory status success! ${responseBody.messages?.joinToString("\n")}"
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostPray() {
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
        val service: PrayInterface = retrofit.create(PrayInterface::class.java)
        val call: Call<RoomDetails> = service.postPray()
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Pray failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Pray success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostRecall() {
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
        val service: RecallInterface = retrofit.create(RecallInterface::class.java)
        val call: Call<RoomDetails> = service.postRecall()
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Recall failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Recall success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    updateGraphDetails(responseBody)
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                    SharedPrefs.saveState()
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostWear(treasure: Treasure) {
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
        val service: WearInterface = retrofit.create(WearInterface::class.java)
        val call: Call<Status> = service.postWear(treasure)
        call.enqueue(object : Callback<Status> {
            override fun onFailure(call: Call<Status>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    val responseBody: Status = response.body() as Status
                    inventoryStatus = responseBody
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Wear failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Wear success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostUndress(treasure: Treasure) {
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
        val service: UndressInterface = retrofit.create(UndressInterface::class.java)
        val call: Call<Status> = service.postUndress(treasure)
        call.enqueue(object : Callback<Status> {
            override fun onFailure(call: Call<Status>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful) {
                    val responseBody: Status = response.body() as Status
                    inventoryStatus = responseBody
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Undress failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Undress success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostChangeName(treasureName: Treasure) {
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
        val service: ChangeNameInterface = retrofit.create(ChangeNameInterface::class.java)
        val call: Call<RoomDetails> = service.postChangeName(treasureName)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Change name failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Change name success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostTransmogrify(treasure: Treasure) {
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
        val service: TransmogrifyInterface = retrofit.create(TransmogrifyInterface::class.java)
        val call: Call<RoomDetails> = service.postTransmogrify(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Transmogrify failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Transmogrify success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostDash(dash: Dash) {
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
        val service: DashInterface = retrofit.create(DashInterface::class.java)
        val call: Call<RoomDetails> = service.postDash(dash)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Dash failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Dash success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    updateGraphDetails(responseBody)
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                    SharedPrefs.saveState()
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostExamineShort(treasure: Treasure) {
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
        val service: ExamineShortInterface = retrofit.create(ExamineShortInterface::class.java)
        val call: Call<ExamineShort> = service.postExamineShort(treasure)
        call.enqueue(object : Callback<ExamineShort> {
            override fun onFailure(call: Call<ExamineShort>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<ExamineShort>, response: Response<ExamineShort>) {
                if (response.isSuccessful) {
                    val responseBody: ExamineShort = response.body() as ExamineShort
                    examineShort = responseBody
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Examine failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Examine success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostDrop(treasure: Treasure) {
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
        val service: DropInterface = retrofit.create(DropInterface::class.java)
        val call: Call<RoomDetails> = service.postDrop(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Drop failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Drop success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
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
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostCarry(treasure: Treasure) {
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
        val service: CarryInterface = retrofit.create(CarryInterface::class.java)
        val call: Call<RoomDetails> = service.postCarry(treasure)
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Carry failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Carry success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostReceive() {
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
        val service: ReceiveInterface = retrofit.create(ReceiveInterface::class.java)
        val call: Call<RoomDetails> = service.postReceive()
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Receive failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Receive success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostWarp() {
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
        val service: WarpInterface = retrofit.create(WarpInterface::class.java)
        val call: Call<RoomDetails> = service.postWarp()
        call.enqueue(object : Callback<RoomDetails> {
            override fun onFailure(call: Call<RoomDetails>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<RoomDetails>, response: Response<RoomDetails>) {
                if (response.isSuccessful) {
                    val responseBody: RoomDetails = response.body() as RoomDetails
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Warp failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Warp success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    updateGraphDetails(responseBody)
                    SharedPrefs.saveState()
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkPostMine(mine: Mine) {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().readTimeout(60, TimeUnit.SECONDS).addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: BcMineInterface = retrofit.create(BcMineInterface::class.java)
        val call: Call<Transaction> = service.postMine(mine)
        call.enqueue(object : Callback<Transaction> {
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                if (response.isSuccessful) {
                    val responseBody: Transaction = response.body() as Transaction
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    var message: String = "Code ${response.code()}: "
                    message += if (responseBody.errors?.isNotEmpty() == true) {
                        "Mine failure!\n${responseBody.errors?.joinToString("\n")}"
                    } else {
                        "Mine success!\n${responseBody.messages?.joinToString("\n")}"
                    }
                    text_log.append(message + "\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, message)
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkGetLastProof() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: BcLastProofInterface = retrofit.create(BcLastProofInterface::class.java)
        val call: Call<Proof> = service.getLastProof()
        call.enqueue(object : Callback<Proof> {
            override fun onFailure(call: Call<Proof>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Proof>, response: Response<Proof>) {
                if (response.isSuccessful) {
                    val responseBody: Proof = response.body() as Proof
                    cooldownAmount = responseBody.cooldown
                    proof = responseBody
                    text_room_info.text = responseBody.toString()
                    text_log.append("Code ${response.code()}: Last proof success!\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, "${response.code()}: Last proof success!")
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun networkGetBalance() {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://lambda-treasure-hunt.herokuapp.com/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder().addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("Authorization", "Token $authorizationToken").build()
                chain.proceed(request)
            }.build())
            .build()
        val service: BcGetBalanceInterface = retrofit.create(BcGetBalanceInterface::class.java)
        val call: Call<Balance> = service.getBalance()
        call.enqueue(object : Callback<Balance> {
            override fun onFailure(call: Call<Balance>, t: Throwable) {
                text_log.append("${t.message}\n")
                scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                UserInteraction.inform(applicationContext, t.message ?: "Failure")
            }

            override fun onResponse(call: Call<Balance>, response: Response<Balance>) {
                if (response.isSuccessful) {
                    val responseBody: Balance = response.body() as Balance
                    cooldownAmount = responseBody.cooldown
                    text_room_info.text = responseBody.toString()
                    text_log.append("Code ${response.code()}: Get balance success!\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, "${response.code()}: Get balance success!")
                } else {
                    val errorBodyTypeCast: Type = object : TypeToken<ErrorBody>() {}.type
                    val errorBody: ErrorBody = Gson().fromJson(response.errorBody()?.string(), errorBodyTypeCast)
                    val errorText = "${response.message()} ${response.code()}:\n$errorBody"
                    cooldownAmount = errorBody.cooldown
                    text_log.append("$errorText\n")
                    scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
                    UserInteraction.inform(applicationContext, errorText)
                }
                showCooldownTimer()
            }
        })
    }

    private fun moveInDirection(direction: String) {
        if (isInitDataDownloaded()) {
            val nextRoom: String? = anticipateNextRoom(direction)
            val moveWisely: MoveWisely = MoveWisely(direction, nextRoom)
            val networkRunnable: Runnable = Runnable {
                if (getCurrentRoomDetails().terrain?.toLowerCase(Locale.getDefault()) == "mountain")
                    networkPostFly(moveWisely)
                else
                    networkPostMove(moveWisely)
            }
            val networkThread = Thread(networkRunnable)
            networkThread.start()
            networkThread.join()
            text_room_info.text = responseRoomInfo
            text_log.append(responseMessage)
            scroll_log.fullScroll(ScrollView.FOCUS_DOWN)
            UserInteraction.inform(applicationContext, responseMessage)
            showCooldownTimer()
        }
    }

    private fun updateGraphDetails(responseBody: RoomDetails?) {
        currentRoomId = responseBody?.roomId ?: 0
        setDarkWorldStatus()
        if (inDarkWorld) {
            if (darkGraph[currentRoomId].isNullOrEmpty())
                darkGraph[currentRoomId] = arrayListOf<Any?>(roomDetails, roomConnections, cellDetails)
            darkGraph[currentRoomId]?.set(0, responseBody)
            darkGraph[currentRoomId]?.set(1, validateRoomConnections(currentRoomId))
            darkGraph[currentRoomId]?.set(2, fillCellDetails(currentRoomId))
        } else {
            if (roomsGraph[currentRoomId].isNullOrEmpty())
                roomsGraph[currentRoomId] = arrayListOf<Any?>(roomDetails, roomConnections, cellDetails)
            roomsGraph[currentRoomId]?.set(0, responseBody)
            roomsGraph[currentRoomId]?.set(1, validateRoomConnections(currentRoomId))
            roomsGraph[currentRoomId]?.set(2, fillCellDetails(currentRoomId))
        }
        view_map.calculateSize()
    }

    private fun setDarkWorldStatus() {
        inDarkWorld = currentRoomId >= 500
    }

    private fun anticipateNextRoom(direction: String): String? {
        val directionAssociations: HashMap<*, *> = if (inDarkWorld) {
            darkGraph[currentRoomId]?.get(1) as HashMap<*, *>
        } else {
            roomsGraph[currentRoomId]?.get(1) as HashMap<*, *>
        }
        return (directionAssociations[direction] as Int?)?.toString()
    }

    private fun setRoomIdForPreviousRoom(direction: String?, roomId: Int?) {
        if (currentRoomId != roomId) {
            if (inDarkWorld) {
                @Suppress("UNCHECKED_CAST")
                (darkGraph[currentRoomId]!![1] as HashMap<String, Int?>)[direction ?: "n"] = roomId
            } else {
                @Suppress("UNCHECKED_CAST")
                (roomsGraph[currentRoomId]!![1] as HashMap<String, Int?>)[direction ?: "n"] = roomId
            }
        }
    }

    private fun getDirectionsFromRoom(roomId: Int?): HashMap<String, Int?> {
        if (roomId == null)
            return hashMapOf()
        val roomTrifecta: ArrayList<Any?> = if (inDarkWorld) {
            darkGraph[roomId] ?: arrayListOf()
        } else {
            roomsGraph[roomId] ?: arrayListOf()
        }
        @Suppress("UNCHECKED_CAST")
        return roomTrifecta[1] as HashMap<String, Int?>
    }

    private fun getDirectionForRoom(roomId: Int?): String? {
        val roomDirections: HashMap<String, Int?> = getDirectionsFromRoom(currentRoomId)
        val directions: Set<String> = roomDirections.filterValues { it == roomId }.keys
        return if (directions.isNotEmpty())
            directions.first()
        else
            null
    }

    private fun validateRoomConnections(roomId: Int?): HashMap<String, Int?> {
        val extractedRoomDetailsExits: List<String>?
        val extractedRoomConnections: HashMap<*, *>
        if (inDarkWorld) {
            extractedRoomDetailsExits = (darkGraph[roomId]?.get(0) as RoomDetails).exits
            extractedRoomConnections = darkGraph[roomId]?.get(1) as HashMap<*, *>
        } else {
            extractedRoomDetailsExits = (roomsGraph[roomId]?.get(0) as RoomDetails).exits
            extractedRoomConnections = roomsGraph[roomId]?.get(1) as HashMap<*, *>
        }
        val validExits: HashMap<String, Int?> = hashMapOf()
        extractedRoomDetailsExits?.forEach {
            validExits[it] = extractedRoomConnections[it] as Int?
        }
        return validExits
    }

    private fun fillCellDetails(roomId: Int?): CellDetails {
        val extractedRoomDetails: RoomDetails = if (inDarkWorld) {
            darkGraph[roomId]?.get(0) as RoomDetails
        } else {
            roomsGraph[roomId]?.get(0) as RoomDetails
        }
        val coordinates: String = extractedRoomDetails.coordinates ?: "(0,0)"
        val coordinatesSplit: List<String> = coordinates.substring(1, coordinates.length - 1).split(",")
        val cellColor: String = Color.TRANSPARENT.toHexString()
        return CellDetails(coordinatesSplit[0].toInt(), coordinatesSplit[1].toInt(), cellColor)
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

    private fun runNextAutomatedStep(keepGoing: Boolean) {
        if (keepGoing)
            timerHandlerUnexplored.post(myRunnableUnexplored)
        else
            timerHandlerSpecific.post(myRunnableSpecific)
    }

    @OptIn(ExperimentalStdlibApi::class)
    val myRunnableUnexplored = Runnable {
        if (automatedPath.isNotEmpty()) {
            var direction: String? = getDirectionForRoom(automatedPath.removeFirst())
            if (direction == null) {
                direction = getCurrentRoomDetails().exits?.random()
            }
            when (direction) {
                "n" -> button_move_north.performClick()
                "s" -> button_move_south.performClick()
                "e" -> button_move_east.performClick()
                "w" -> button_move_west.performClick()
            }
        } else {
            UserInteraction.inform(this, "There is nowhere to go...")
            traversalTimer.cancel()
            return@Runnable
        }
        if (automatedPath.isEmpty()) {
            traversalTimer.cancel()
        }
        if (automatedPath.isEmpty()) {
            moveToUnexploredAutomated()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    val myRunnableSpecific = Runnable {
        val destinationRoom: Int? = automatedPath.last()
        val direction: String? = getDirectionForRoom(automatedPath.removeFirst())
        if (direction == null) {
            UserInteraction.askQuestion(
                this, "Room Not Found", "Problem encountered traversing to room #$destinationRoom!", "Okay", null
            )
            traversalTimer.cancel()
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
        val exploredWisely: Boolean = getCurrentRoomDetails().messages?.last()?.contains("Wise") ?: false
        if ((destinationRoom == currentRoomId) || (destinationRoom == null && !exploredWisely)) {
            UserInteraction.askQuestion(this, "Room Found", "Room #$destinationRoom has been found!", "Okay", null)
        }
    }

    private fun getCurrentRoomDetails(): RoomDetails {
        return if (inDarkWorld) {
            darkGraph[currentRoomId]?.get(0) as RoomDetails
        } else {
            roomsGraph[currentRoomId]?.get(0) as RoomDetails
        }
    }

    private fun moveToUnexploredAutomated(pauseInSeconds: Int = 16) {
        if (!isInitDataDownloaded()) return
        automatedPath = bfs(null)
        traversalTimer = Timer()
        traversalTimer.schedule(object : TimerTask() {
            override fun run() {
                runNextAutomatedStep(true)
            }
        }, pauseInSeconds * 1000L, pauseInSeconds * 1000L)
    }

    private fun isInitDataDownloaded(): Boolean {
        if ((inDarkWorld && darkGraph.isEmpty()) || (!inDarkWorld && roomsGraph.isEmpty()) || (currentRoomId == -1)) {
            UserInteraction.inform(this, "Please do a GET 'Init' first...")
            return false
        }
        return true
    }

    private fun isStatusDataDownloaded(): Boolean {
        if (inventoryStatus.name == null) {
            UserInteraction.inform(this, "Please do a POST 'Status' first...")
            return false
        }
        return true
    }

    private fun isProofDataDownloaded(): Boolean {
        if (proof.proof == null) {
            UserInteraction.inform(this, "Please do a GET 'Last proof' first...")
            return false
        }
        return true
    }

    private fun moveToSpecificRoomAutomated(roomId: Int?, pauseInSeconds: Int = 16) {
        if (!isInitDataDownloaded()) {
            return
        } else if (roomId == currentRoomId) {
            UserInteraction.askQuestion(this, "Room Is Here", "You are already at Room #${roomId}!", "Okay", null)
            return
        }
        automatedPath = bfs(roomId)
        traversalTimer = Timer()
        traversalTimer.schedule(object : TimerTask() {
            override fun run() {
                runNextAutomatedStep(false)
            }
        }, 0, pauseInSeconds * 1000L)
    }

    private fun bfs(destinationRoom: Int?): ArrayList<Int?> {
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