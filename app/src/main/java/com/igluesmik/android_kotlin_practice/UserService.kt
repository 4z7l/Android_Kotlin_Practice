package com.igluesmik.android_kotlin_practice

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserService {
    @GET("users/{name}")
    fun getUser(
        @Path("name") userName: String
    ): Call<User>
}