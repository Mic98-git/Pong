package com.example.ponggame

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class DatabaseImpl : Database {

    override fun getAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    override fun getStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    override fun getDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    override fun loginUser(email : String, password : String) {
        getAuthInstance().signInWithEmailAndPassword(email, password)
    }

    override fun registerUser(email : String, password : String) {
        getAuthInstance().createUserWithEmailAndPassword(email, password)
    }

    override fun logOut() {
        getAuthInstance().signOut()
    }

    override fun getCurrentUserId(): String {
        return getAuthInstance().currentUser?.uid.toString()
    }

    override fun getCurrentUserInfo() {
        TODO("Not yet implemented")
    }

    override fun createNewUser(username: String, email: String): User {
        return User(username, getCurrentUserId(), email, 0)
    }

    override fun getUsers(): DatabaseReference {
        return getDatabaseInstance().getReference("Users")
    }

    override fun uploadUserImage(imageUri: Uri) {
        val reference = getStorageInstance().reference.child("profile_pictures")
            .child(getCurrentUserId())
        reference.putFile(imageUri)
    }
}