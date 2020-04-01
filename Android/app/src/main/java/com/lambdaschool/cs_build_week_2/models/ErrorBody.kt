package com.lambdaschool.cs_build_week_2.models

class ErrorBody {
    var cooldown: Double? = null
    var errors: List<String>? = null

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        val sb: StringBuilder = java.lang.StringBuilder()
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        return sb.toString()
    }
}