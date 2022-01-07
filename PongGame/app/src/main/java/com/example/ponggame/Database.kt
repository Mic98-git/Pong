package com.example.ponggame

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

interface Database {

    fun createNewUser (username : String, email : String): Task<Void>

    fun registerUser(email : String, password: String ): Task<AuthResult>

    fun loginUser(email : String, password : String): Task<AuthResult>

    fun logOut()

    fun getCurrentUserId(): String

    fun getUsersReference(): DatabaseReference

    fun getStorageInstance(): FirebaseStorage

    fun getDatabaseInstance(): FirebaseDatabase

    fun getAuthInstance(): FirebaseAuth

    fun getReferenceToUsersProfilePictures(): StorageReference

    fun uploadProfilePicture(imageUri: Uri)

    fun getProfilePicture(localFile : File): FileDownloadTask

    fun updateUserScore(update : Int)

    fun updateProfilePicture(imageUri: Uri)

    fun updateUserUsername(username: String)

}