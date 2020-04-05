package com.lambdaschool.cs_build_week_2.utils

import java.security.MessageDigest

object Mining {
    fun proofOfWork(lastProof: Int?, difficulty: Int?): Int {
        val repeatedZeroes: String = "0".repeat(difficulty ?: 1)
        var proofCandidate: Int = -1
        do {
            proofCandidate++
            val proofHash: String = "$lastProof$proofCandidate".sha256()
        } while (!proofHash.startsWith(repeatedZeroes))
        return proofCandidate
    }

    private fun hashString(input: String, algorithm: String): String {
        return MessageDigest
            .getInstance(algorithm)
            .digest(input.toByteArray())
            .fold("", { str, it -> str + "%02x".format(it) })
    }

    private fun String.md5(): String {
        return hashString(this, "MD5")
    }

    private fun String.sha256(): String {
        return hashString(this, "SHA-256")
    }
}