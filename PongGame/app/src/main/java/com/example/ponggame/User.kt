package com.example.ponggame

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(var username: String ?=null, val uid: String?=null, val email: String?=null, var score: Int?=null)