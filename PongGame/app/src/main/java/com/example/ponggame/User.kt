package com.example.ponggame

import com.google.firebase.database.IgnoreExtraProperties
import com.google.gson.annotations.SerializedName

@IgnoreExtraProperties
data class User(@SerializedName("username") var username: String ?=null,
                @SerializedName("uid") val uid: String?=null,
                @SerializedName("email") val email: String?=null,
                @SerializedName("score") var score: Int?=null)