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
}