package com.example.ponggame

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.File

object DatabaseImpl : Database {

    override fun getAuthInstance(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    override fun getStorageInstance(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }

    override fun getDatabaseInstance(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    override fun getReferenceToUsersProfilePictures(): StorageReference {
        return getStorageInstance().reference.child("profile_pictures")
    }

    override fun loginUser(email : String, password : String): Task<AuthResult> {
        return getAuthInstance().signInWithEmailAndPassword(email, password)
    }

    override fun resetPassword(email: String): Task<Void> {
        return getAuthInstance().sendPasswordResetEmail(email)
    }

    override fun sendEmailVerification(): Task<Void> {
        return getAuthInstance().currentUser!!.sendEmailVerification()
    }

    override fun registerUser(email : String, password : String): Task<AuthResult> {
        return getAuthInstance().createUserWithEmailAndPassword(email, password)
    }

    override fun logOut() {
        getAuthInstance().signOut()
    }

    override fun getCurrentUserId(): String {
        return getAuthInstance().currentUser?.uid.toString()
    }

    override fun createNewUser(username: String, email: String): Task<Void> {
        val user = User(username, getCurrentUserId(), email, 0)
        return getUsersReference().child(getCurrentUserId()).setValue(user)
    }

    override fun getUsersReference(): DatabaseReference {
        return getDatabaseInstance().getReference("Users")
    }

    override fun getProfilePicture(localFile: File) : FileDownloadTask {
        return getReferenceToUsersProfilePictures().child(getCurrentUserId()).getFile(localFile)
    }

    override fun uploadProfilePicture(imageUri : Uri) {
        getReferenceToUsersProfilePictures().child(getCurrentUserId()).putFile(imageUri)
    }

    override fun updateUserUsername(username : String) {
        getUsersReference().child(getCurrentUserId()).child("username").setValue(username)
    }

    override fun updateUserScore(update: Int) {
        getUsersReference().child(getCurrentUserId()).child("score").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val score = (snapshot.value as Long).toInt()
                getUsersReference().child(getCurrentUserId()).child("score").setValue(score.plus(update))
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseImpl", "Error setting current user data")
            }
        })
    }

    override fun updateProfilePicture(imageUri: Uri) {
        val profilePicture = getReferenceToUsersProfilePictures().child(getCurrentUserId())
        profilePicture.metadata
            .addOnSuccessListener {
            profilePicture.delete().addOnCompleteListener { deleteImage ->
                if (deleteImage.isSuccessful) {
                    uploadProfilePicture(imageUri)
                }
            }
            }
            .addOnFailureListener {
                Log.d("DatabaseImpl", "no profile images")
                uploadProfilePicture(imageUri)
            }
    }

    override fun deleteUser() {
        getUsersReference().child(getCurrentUserId()).removeValue()
        getReferenceToUsersProfilePictures().child(getCurrentUserId()).delete()
        getAuthInstance().currentUser!!.delete()
    }

}

    /*override fun setCurrentLoggedUser() {
        getUsersReference().child(getAuthInstance().currentUser?.uid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = snapshot.child("username").value.toString()
                val email = snapshot.child("email").value.toString()
                val uid = snapshot.child("uid").value.toString()
                val score = snapshot.child("score").value.toString().toInt()
                currentUser = User(username, uid, email, score)
                Log.d("DatabaseImpl", "Current logged user setted")
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("DatabaseImpl", "Error setting current user data")
            }
        })*/
