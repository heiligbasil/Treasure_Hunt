package com.lambdaschool.cs_build_week_2.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Room {
    @SerializedName("room_id")
    @Expose
    var roomId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("coordinates")
    @Expose
    var coordinates: String? = null

    @SerializedName("exits")
    @Expose
    var exits: List<String>? = null

    @SerializedName("cooldown")
    @Expose
    var cooldown: Double? = null

    @SerializedName("errors")
    @Expose
    var errors: List<String>? = null

    @SerializedName("messages")
    @Expose
    var messages: List<String>? = null
}