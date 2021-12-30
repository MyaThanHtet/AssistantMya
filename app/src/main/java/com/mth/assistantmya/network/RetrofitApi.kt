/*
 * Copyright (c) Mya Than Htet 2021.
 */

package com.mth.assistantmya.network

import com.mth.assistantmya.model.BotMessage
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url


interface RetrofitApi {


    @GET
    fun getMessage(@Url url: String): Call<BotMessage>


}



