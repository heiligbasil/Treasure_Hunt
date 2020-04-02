package com.lambdaschool.cs_build_week_2.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Status {
    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("cooldown")
    @Expose
    var cooldown: Double? = null

    @SerializedName("encumbrance")
    @Expose
    var encumbrance: Int? = null

    @SerializedName("strength")
    @Expose
    var strength: Int? = null

    @SerializedName("speed")
    @Expose
    var speed: Int? = null

    @SerializedName("gold")
    @Expose
    var gold: Int? = null

    @SerializedName("bodywear")
    @Expose
    var bodywear: String? = null

    @SerializedName("footwear")
    @Expose
    var footwear: String? = null

    @SerializedName("inventory")
    @Expose
    var inventory: List<String>? = null

    @SerializedName("abilities")
    @Expose
    var abilities: List<String>? = null

    @SerializedName("status")
    @Expose
    var status: List<String>? = null

    @SerializedName("has_mined")
    @Expose
    var hasMined: Boolean? = null

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
        sb.appendln("Inventory for: $name")
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Encumbrance: $encumbrance")
        sb.appendln("Strength: $strength")
        sb.appendln("Speed: $speed")
        sb.appendln("Gold: $gold")
        sb.appendln("Bodywear: $bodywear")
        sb.appendln("Footwear: $footwear")
        sb.appendln("Inventory: ${inventory?.joinToString()}")
        sb.appendln("Abilities: ${abilities?.joinToString()}")
        sb.appendln("Status: ${status?.joinToString()}")
        sb.appendln("Has mined?: $hasMined")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}