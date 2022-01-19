package com.example.ponggame.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.databinding.FragmentUserProfileBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import java.io.File

class UserProfileFragment : Fragment() {

    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("UserProfileFragment", "User profile fragment created!")
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        setActivityTitle("User Profile")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.constraintLayoutProfileImage
        setProfilePicture()

        if (DatabaseImpl.getCurrentUserId().isNotEmpty()) {
            DatabaseImpl.getUsersReference().child(DatabaseImpl.getCurrentUserId()).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    view.findViewById<TextView>(R.id.user_email).text = snapshot.child("email").value.toString()
                    view.findViewById<TextView>(R.id.user_username).text = snapshot.child("username").value.toString()
                    view.findViewById<TextView>(R.id.user_score).text = snapshot.child("score").value.toString()
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        context,
                        "Error retrieving your data",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        val updateProfileButton = view.findViewById<Button>(R.id.update_profile_button)
        updateProfileButton.setOnClickListener {
        }

        val signOutButton = view.findViewById<Button>(R.id.log_out_button)
        signOutButton.setOnClickListener {
            logOut()
            view.findNavController().navigate(
                UserProfileFragmentDirections.actionUserProfileFragmentToLoginFragment()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }

    private fun logOut() {
        DatabaseImpl.logOut()
        Toast.makeText(
                context,
                "You are successfully logged out",
                Toast.LENGTH_SHORT
            ).show()
    }

    private fun setProfilePicture() {
        val localFile = File.createTempFile("tempImage", "")
        DatabaseImpl.getProfilePicture(localFile).addOnSuccessListener {
            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
            binding.profileImageIcon.setImageBitmap(bitmap)
        }
    }
}