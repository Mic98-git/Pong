package com.example.ponggame

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User( val username: String ?=null, val password: String?=null, val email: String?=null, val score: Long?=null)