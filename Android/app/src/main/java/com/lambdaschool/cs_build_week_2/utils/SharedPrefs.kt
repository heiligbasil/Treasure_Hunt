package com.lambdaschool.cs_build_week_2.utils

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.lambdaschool.cs_build_week_2.models.CellDetails
import com.lambdaschool.cs_build_week_2.models.RoomDetails
import com.lambdaschool.cs_build_week_2.views.MainActivity
import java.lang.reflect.Type

object SharedPrefs {

    fun saveState() {
        val gson: Gson = GsonBuilder().serializeNulls().create()
        MainActivity.roomsGraph.forEach {
            val arrayList: ArrayList<Any?> = MainActivity.roomsGraph[it.key] ?: arrayListOf()
            val roomDetailsToJson: String = gson.toJson(arrayList[0])
            val roomConnectionsToJson: String = gson.toJson(arrayList[1])
            val cellDetailsToJson: String = gson.toJson(arrayList[2])
            MainActivity.preferences.edit().putString("${it.key}_room_details", roomDetailsToJson).apply()
            MainActivity.preferences.edit().putString("${it.key}_room_connections", roomConnectionsToJson).apply()
            MainActivity.preferences.edit().putString("${it.key}_cell_details", cellDetailsToJson).apply()
        }
        MainActivity.darkGraph.forEach {
            val arrayList: ArrayList<Any?> = MainActivity.darkGraph[it.key] ?: arrayListOf()
            val roomDetailsToJson: String = gson.toJson(arrayList[0])
            val roomConnectionsToJson: String = gson.toJson(arrayList[1])
            val cellDetailsToJson: String = gson.toJson(arrayList[2])
            MainActivity.preferencesDark.edit().putString("${it.key}_room_details", roomDetailsToJson).apply()
            MainActivity.preferencesDark.edit().putString("${it.key}_room_connections", roomConnectionsToJson).apply()
            MainActivity.preferencesDark.edit().putString("${it.key}_cell_details", cellDetailsToJson).apply()
        }
        return
    }

    fun loadState() {
        val gson: Gson = GsonBuilder().serializeNulls().create()
        MainActivity.roomsGraph.clear()
        val allSharedPrefsData: MutableMap<String, *> = MainActivity.preferences.all
        allSharedPrefsData.forEach {
            val choppedKey: List<String> = it.key.split("_", limit = 2)
            val roomId: Int = choppedKey[0].toInt()
            if (MainActivity.roomsGraph[roomId].isNullOrEmpty()) {
                MainActivity.roomsGraph[roomId] = arrayListOf<Any?>(
                    MainActivity.roomDetails, MainActivity.roomConnections, MainActivity.cellDetails
                )
            }
            val keyName: String = choppedKey[1]
            if (keyName == "room_details") {
                val roomDetailsTypeCast: Type = object : TypeToken<RoomDetails>() {}.type
                val roomDetailsIsBack: RoomDetails = gson.fromJson(it.value.toString(), roomDetailsTypeCast)
                MainActivity.roomsGraph[roomId]?.set(0, roomDetailsIsBack)
            } else if (keyName == "room_connections") {
                val roomConnectionsTypeCast: Type = object : TypeToken<HashMap<String, Int?>>() {}.type
                val roomConnectionsIsBack: HashMap<String, Int?> = gson.fromJson(it.value.toString(), roomConnectionsTypeCast)
                MainActivity.roomsGraph[roomId]?.set(1, roomConnectionsIsBack)
            } else if (keyName == "cell_details") {
                val cellDetailsTypeCast: Type = object : TypeToken<CellDetails>() {}.type
                val cellDetailsIsBack: CellDetails = gson.fromJson(it.value.toString(), cellDetailsTypeCast)
                MainActivity.roomsGraph[roomId]?.set(2, cellDetailsIsBack)
            }
        }
        MainActivity.darkGraph.clear()
        val allSharedPrefsDataDark: MutableMap<String, *> = MainActivity.preferencesDark.all
        allSharedPrefsDataDark.forEach {
            val choppedKey: List<String> = it.key.split("_", limit = 2)
            val roomId: Int = choppedKey[0].toInt()
            if (MainActivity.darkGraph[roomId].isNullOrEmpty()) {
                MainActivity.darkGraph[roomId] = arrayListOf<Any?>(
                    MainActivity.roomDetails, MainActivity.roomConnections, MainActivity.cellDetails
                )
            }
            val keyName: String = choppedKey[1]
            if (keyName == "room_details") {
                val roomDetailsTypeCast: Type = object : TypeToken<RoomDetails>() {}.type
                val roomDetailsIsBack: RoomDetails = gson.fromJson(it.value.toString(), roomDetailsTypeCast)
                MainActivity.darkGraph[roomId]?.set(0, roomDetailsIsBack)
            } else if (keyName == "room_connections") {
                val roomConnectionsTypeCast: Type = object : TypeToken<HashMap<String, Int?>>() {}.type
                val roomConnectionsIsBack: HashMap<String, Int?> = gson.fromJson(it.value.toString(), roomConnectionsTypeCast)
                MainActivity.darkGraph[roomId]?.set(1, roomConnectionsIsBack)
            } else if (keyName == "cell_details") {
                val cellDetailsTypeCast: Type = object : TypeToken<CellDetails>() {}.type
                val cellDetailsIsBack: CellDetails = gson.fromJson(it.value.toString(), cellDetailsTypeCast)
                MainActivity.darkGraph[roomId]?.set(2, cellDetailsIsBack)
            }
        }
        return
    }
}