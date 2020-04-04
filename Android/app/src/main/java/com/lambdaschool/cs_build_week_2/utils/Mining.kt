package com.lambdaschool.cs_build_week_2.utils

import java.security.MessageDigest

object Mining {
    fun proofOfWork(lastProof: Int, difficulty: Int): Int {
        val lastProofHash: String = lastProof.toString().sha256()
        val repeatedZeroes = "0".repeat(difficulty)
        var proofCandidate: Int = 0
        do {
            val proofHash: String = proofCandidate.toString().sha256()
            proofCandidate++
        } while (!lastProofHash.startsWith(repeatedZeroes) && !proofHash.startsWith(repeatedZeroes))
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