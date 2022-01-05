package com.example.ponggame

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

interface Database {

    fun createNewUser (username : String, email : String): User

    fun registerUser(email : String, password: String )

    fun loginUser(email : String, password : String)

    fun logOut()

    fun getCurrentUserInfo()

    fun getCurrentUserId(): String

    fun uploadUserImage(imageUri: Uri)

    fun getUsers(): DatabaseReference

    fun getStorageInstance(): FirebaseStorage

    fun getDatabaseInstance(): FirebaseDatabase

    fun getAuthInstance(): FirebaseAuth

}