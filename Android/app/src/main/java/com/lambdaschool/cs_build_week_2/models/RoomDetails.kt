package com.lambdaschool.cs_build_week_2.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class RoomDetails {
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

    @SerializedName("elevation")
    @Expose
    var elevation: Int? = null

    @SerializedName("terrain")
    @Expose
    var terrain: String? = null

    @SerializedName("players")
    @Expose
    var players: List<String>? = null

    @SerializedName("items")
    @Expose
    var items: List<String>? = null

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

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        val sb: StringBuilder = java.lang.StringBuilder()
        sb.appendln("Details for Room: $roomId")
        sb.appendln("Title: $title")
        sb.appendln("Description: $description")
        sb.appendln("Coordinates: $coordinates")
        sb.appendln("Elevation: $elevation")
        sb.appendln("Terrain: $terrain")
        sb.appendln("Players: ${players?.joinToString()}")
        sb.appendln("Items: ${items?.joinToString()}")
        sb.appendln("Exits: ${exits?.joinToString()}")
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}