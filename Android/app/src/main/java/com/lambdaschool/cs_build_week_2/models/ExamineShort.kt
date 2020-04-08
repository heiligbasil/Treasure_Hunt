package com.lambdaschool.cs_build_week_2.models

class ExamineShort {
    var name: String? = null
    var description: String? = null
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
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}