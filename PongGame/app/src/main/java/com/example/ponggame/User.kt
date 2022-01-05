package com.example.ponggame

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User( val username: String ?=null, val uid: String?=null, val email: String?=null, val score: Long?=null)