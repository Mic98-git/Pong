package com.example.ponggame

import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {

    @FormUrlEncoded
    @POST("signup")
    suspend fun registerUser(
        @Field("email") email:String,
        @Field("password") password:String,
    ): User

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email: String,
        @Field("password") password: String,
    ): User

    @GET("currentUser")
    suspend fun currentUser(): User

    @GET("Users")
    suspend fun getUsersList(): HashMap<String, User>

    @GET("Users/{user_id}")
    suspend fun getUser(
        @Path(value = "user_id", encoded = true) uid: String
    ): User

    @FormUrlEncoded
    @POST("Users/{user_id}")
    suspend fun createUser(
        @Path(value = "user_id", encoded = true) uid: String,
        @Field("email") email: String,
        @Field("username") username: String,
        @Field("score") score: Int,
    ): Response<Unit>

    @FormUrlEncoded
    @PUT("Users/{user_id}")
    suspend fun updateUserUsername(
        @Path(value = "user_id", encoded = true) uid: String,
        @Field("username") username: String
    ): Response<Unit>

    @FormUrlEncoded
    @PUT("Users/{user_id}")
    suspend fun updateUserScore(
        @Path(value = "user_id", encoded = true) uid: String,
        @Field("score") score: Int
    ): Response<Unit>

    @FormUrlEncoded
    @POST("Users/resetPassword")
    suspend fun resetPassword(
        @Field("email") email: String
    ): Response<Unit>
}