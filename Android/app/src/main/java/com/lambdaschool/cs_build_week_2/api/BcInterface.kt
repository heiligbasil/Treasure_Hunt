package com.lambdaschool.cs_build_week_2.api

import com.lambdaschool.cs_build_week_2.models.Balance
import com.lambdaschool.cs_build_week_2.models.Mine
import com.lambdaschool.cs_build_week_2.models.Proof
import com.lambdaschool.cs_build_week_2.models.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface BcMineInterface {
    @POST("bc/mine/")
    fun postMine(@Body mine: Mine): Call<Transaction>
}

interface BcLastProofInterface {
    @GET("bc/last_proof/")
    fun getLastProof(): Call<Proof>
}

interface BcGetBalanceInterface {
    @GET("bc/get_balance/")
    fun getBalance(): Call<Balance>
}

