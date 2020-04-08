package com.lambdaschool.cs_build_week_2.models

class Examine {
    var name: String? = null
    var description: String? = null
    var weight: Int? = null
    var itemtype: String? = null
    var level: Int? = null
    var exp: Int? = null
    var attributes: String? = null
    var cooldown: Double? = null
    var errors: List<String>? = null
    var messages: List<String>? = null

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        val sb: StringBuilder = java.lang.StringBuilder()
        sb.appendln("Name: $name")
        sb.appendln("Description: $description")
        sb.appendln("Weight: $weight")
        sb.appendln("ItemType: $itemtype")
        sb.appendln("Level: $level")
        sb.appendln("Exp: $exp")
        sb.appendln("Attributes: $attributes")
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}