package com.lambdaschool.cs_build_week_2.api

import com.lambdaschool.cs_build_week_2.models.Proof
import retrofit2.Call
import retrofit2.http.GET

interface BcMineInterface {
    @GET("/bc/mine/")
    fun getMove(): Call<Proof>
}

interface BcTotalsInterface {
    @GET("/bc/totals/")
    fun getMove(): Call<Proof>
}

interface BcLastProofInterface {
    @GET("/bc/last_proof/")
    fun getMove(): Call<Proof>
}

interface BcGetBalanceInterface {
    @GET("/bc/get_balance/")
    fun getMove(): Call<Proof>
}

