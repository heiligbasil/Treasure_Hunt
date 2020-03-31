package com.lambdaschool.cs_build_week_2.api

import com.lambdaschool.cs_build_week_2.models.RoomDetails
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface InitInterface {
    @GET("adv/init/")
    fun getRoomInit(): Call<RoomDetails>
}

interface MoveInterface {
    @POST("adv/move/")
    fun getMove(@Body jsonString: String): Call<RoomDetails>
}

interface TakeInterface {
    @GET("adv/take/")
    fun getMove(): Call<RoomDetails>
}

interface DropInterface {
    @GET("adv/drop/")
    fun getMove(): Call<RoomDetails>
}

interface StatusInterface {
    @GET("adv/status/")
    fun getMove(): Call<RoomDetails>
}

interface BuyInterface {
    @GET("adv/buy/")
    fun getMove(): Call<RoomDetails>
}

interface SellInterface {
    @GET("adv/sell/")
    fun getMove(): Call<RoomDetails>
}

interface WearInterface {
    @GET("adv/wear/")
    fun getMove(): Call<RoomDetails>
}

interface UndressInterface {
    @GET("adv/undress/")
    fun getMove(): Call<RoomDetails>
}

interface ExamineInterface {
    @GET("adv/examine/")
    fun getMove(): Call<RoomDetails>
}

interface ChangeNameInterface {
    @GET("adv/change_name/")
    fun getMove(): Call<RoomDetails>
}

interface PrayInterface {
    @GET("adv/pray/")
    fun getMove(): Call<RoomDetails>
}

interface FlyInterface {
    @GET("adv/fly/")
    fun getMove(): Call<RoomDetails>
}

interface DashInterface {
    @GET("adv/dash/")
    fun getMove(): Call<RoomDetails>
}

interface PlayerStateInterface {
    @GET("adv/player_state/")
    fun getMove(): Call<RoomDetails>
}

interface TransmogrifyInterface {
    @GET("adv/transmogrify/")
    fun getMove(): Call<RoomDetails>
}

interface CarryInterface {
    @GET("adv/carry/")
    fun getMove(): Call<RoomDetails>
}

interface ReceiveInterface {
    @GET("adv/receive/")
    fun getMove(): Call<RoomDetails>
}

interface WarpInterface {
    @GET("adv/warp/")
    fun getMove(): Call<RoomDetails>
}

interface RecallInterface {
    @GET("adv/recall/")
    fun getMove(): Call<RoomDetails>
}
