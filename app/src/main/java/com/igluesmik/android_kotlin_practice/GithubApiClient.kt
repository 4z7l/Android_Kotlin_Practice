package com.igluesmik.android_kotlin_practice

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GithubApiClient {
    companion object {
        private const val BASE_URL = "https://api.github.com/"

        private fun getInstance(): Retrofit {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        fun <T> create(service: Class<T>): T {
            return getInstance().create(service)
        }
    }

}