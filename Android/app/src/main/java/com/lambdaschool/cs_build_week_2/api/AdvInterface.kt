package com.lambdaschool.cs_build_week_2.api

import com.lambdaschool.cs_build_week_2.models.MoveWisely
import com.lambdaschool.cs_build_week_2.models.RoomDetails
import com.lambdaschool.cs_build_week_2.models.Status
import com.lambdaschool.cs_build_week_2.models.TakeTreasure
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
    fun postMove(@Body moveWisely: MoveWisely): Call<RoomDetails>
}

interface TakeInterface {
    @POST("adv/take/")
    fun postTake(@Body takeTreasure: TakeTreasure): Call<RoomDetails>
}

interface DropInterface {
    @GET("adv/drop/")
    fun getDrop(): Call<RoomDetails>
}

interface StatusInterface {
    @POST("adv/status/")
    fun postStatus(): Call<Status>
}

interface BuyInterface {
    @GET("adv/buy/")
    fun getBuy(): Call<RoomDetails>
}

interface SellInterface {
    @GET("adv/sell/")
    fun getSell(): Call<RoomDetails>
}

interface WearInterface {
    @GET("adv/wear/")
    fun getWear(): Call<RoomDetails>
}

interface UndressInterface {
    @GET("adv/undress/")
    fun getUndress(): Call<RoomDetails>
}

interface ExamineInterface {
    @GET("adv/examine/")
    fun getExamine(): Call<RoomDetails>
}

interface ChangeNameInterface {
    @GET("adv/change_name/")
    fun getChangeName(): Call<RoomDetails>
}

interface PrayInterface {
    @GET("adv/pray/")
    fun getPray(): Call<RoomDetails>
}

interface FlyInterface {
    @GET("adv/fly/")
    fun getFly(): Call<RoomDetails>
}

interface DashInterface {
    @GET("adv/dash/")
    fun getDash(): Call<RoomDetails>
}

interface PlayerStateInterface {
    @GET("adv/player_state/")
    fun getPlayerState(): Call<RoomDetails>
}

interface TransmogrifyInterface {
    @GET("adv/transmogrify/")
    fun getTransmogrify(): Call<RoomDetails>
}

interface CarryInterface {
    @GET("adv/carry/")
    fun getCarry(): Call<RoomDetails>
}

interface ReceiveInterface {
    @GET("adv/receive/")
    fun getReceive(): Call<RoomDetails>
}

interface WarpInterface {
    @GET("adv/warp/")
    fun getWarp(): Call<RoomDetails>
}

interface RecallInterface {
    @GET("adv/recall/")
    fun getRecall(): Call<RoomDetails>
}
