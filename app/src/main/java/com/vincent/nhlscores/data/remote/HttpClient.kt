package com.vincent.nhlscores.data.remote

import android.util.Log
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object HttpClient {
    private val logger = HttpLoggingInterceptor { msg -> Log.d("HTTP", msg) }
        .apply { level = HttpLoggingInterceptor.Level.BODY }

    private val okHttp = OkHttpClient.Builder()
        .addInterceptor(logger)
        .build()

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private fun retrofit(base: String): Retrofit =
        Retrofit.Builder()
            .baseUrl(base)
            .client(okHttp)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

    val webApi: NhlWebApi = retrofit("https://api-web.nhle.com/").create(NhlWebApi::class.java)

    val statsApi: NhlStatsApi = retrofit("https://api.nhle.com/").create(NhlStatsApi::class.java)
}

