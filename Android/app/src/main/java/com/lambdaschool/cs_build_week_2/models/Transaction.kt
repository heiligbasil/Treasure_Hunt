package com.lambdaschool.cs_build_week_2.models

class Transaction {
    var index: Int? = null
    var transactions: String? = null
    var proof: Int? = null
    var previousHash: String? = null
    var cooldown: Double? = null
    var messages: List<String>? = null
    var errors: List<Any>? = null

    /**
     * Returns a string representation of the object.
     */
    override fun toString(): String {
        val sb: StringBuilder = java.lang.StringBuilder()
        sb.appendln("Index: $index")
        sb.appendln("Transactions: $transactions")
        sb.appendln("Proof: $proof")
        sb.appendln("Previous hash: $previousHash")
        sb.appendln("Cooldown: $cooldown")
        sb.appendln("Errors: ${errors?.joinToString()}")
        sb.append("Messages: ${messages?.joinToString()}")
        return sb.toString()
    }
}