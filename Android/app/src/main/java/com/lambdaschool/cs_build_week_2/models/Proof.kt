package com.lambdaschool.cs_build_week_2.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Proof {
    @SerializedName("proof")
    @Expose
    var proof: Int? = null

    @SerializedName("difficulty")
    @Expose
    var difficulty: Int? = null

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
        sb.appendln("Proof: $proof")
        sb.appendln("Difficulty: $difficulty")
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}