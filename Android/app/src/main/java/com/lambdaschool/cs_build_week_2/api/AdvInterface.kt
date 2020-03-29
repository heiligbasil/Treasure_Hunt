package com.lambdaschool.cs_build_week_2.api

import com.lambdaschool.cs_build_week_2.models.Room
import retrofit2.Call
import retrofit2.http.GET

interface AdvInitInterface {
    @GET("adv/init/")
    fun getRoomInit(): Call<Room>
}

interface MoveInterface {
    @GET("adv/move/")
    fun getMove(): Call<Room>
}

interface TakeInterface {
    @GET("adv/take/")
    fun getMove(): Call<Room>
}

interface DropInterface {
    @GET("adv/drop/")
    fun getMove(): Call<Room>
}

interface StatusInterface {
    @GET("adv/status/")
    fun getMove(): Call<Room>
}

interface BuyInterface {
    @GET("adv/buy/")
    fun getMove(): Call<Room>
}

interface SellInterface {
    @GET("adv/sell/")
    fun getMove(): Call<Room>
}

interface WearInterface {
    @GET("adv/wear/")
    fun getMove(): Call<Room>
}

interface UndressInterface {
    @GET("adv/undress/")
    fun getMove(): Call<Room>
}

interface ExamineInterface {
    @GET("adv/examine/")
    fun getMove(): Call<Room>
}

interface ChangeNameInterface {
    @GET("adv/change_name/")
    fun getMove(): Call<Room>
}

interface PrayInterface {
    @GET("adv/pray/")
    fun getMove(): Call<Room>
}

interface FlyInterface {
    @GET("adv/fly/")
    fun getMove(): Call<Room>
}

interface DashInterface {
    @GET("adv/dash/")
    fun getMove(): Call<Room>
}

interface PlayerStateInterface {
    @GET("adv/player_state/")
    fun getMove(): Call<Room>
}

interface TransmogrifyInterface {
    @GET("adv/transmogrify/")
    fun getMove(): Call<Room>
}

interface CarryInterface {
    @GET("adv/carry/")
    fun getMove(): Call<Room>
}

interface ReceiveInterface {
    @GET("adv/receive/")
    fun getMove(): Call<Room>
}

interface WarpInterface {
    @GET("adv/warp/")
    fun getMove(): Call<Room>
}

interface RecallInterface {
    @GET("adv/recall/")
    fun getMove(): Call<Room>
}
