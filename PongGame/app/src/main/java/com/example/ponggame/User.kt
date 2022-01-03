package com.example.ponggame

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User( val username: String, val password: String, val email: String, val score: Long)