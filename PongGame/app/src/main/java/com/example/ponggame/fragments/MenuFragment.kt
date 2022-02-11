package com.example.ponggame.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import com.example.ponggame.DatabaseImpl
import com.example.ponggame.R
import com.example.ponggame.RetrofitClient
import com.example.ponggame.databinding.FragmentMenuBinding
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader


class MenuFragment : Fragment() {
    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var rankButton: ImageView
    private lateinit var myProfileButton: ImageView
    private lateinit var newGameButton: ImageView
    private lateinit var profileImage: CircleImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        Log.d("MenuFragment", "Menu Fragment created!")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        setActivityTitle("Menu")
        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        constraintLayout = binding.menuConstraintLayout
        profileImage = view.findViewById(R.id.user_image)

        /*
        if (DatabaseImpl.getCurrentUserId().isNotEmpty()) {
            DatabaseImpl.getUsersReference().child(DatabaseImpl.getCurrentUserId()).addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    view.findViewById<TextView>(R.id.username_text_view).text = snapshot.child("username").value.toString()
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
         */
        val localFile = File.createTempFile("tempImage", "")
        CoroutineScope(Job()).launch(Dispatchers.Main) {
            var username = "null"
            val currentUser = RetrofitClient.instance.currentUser()
            if (currentUser.uid!!.isNotEmpty()) {
                withContext(Dispatchers.IO) {
                    username = RetrofitClient.instance.getUser(currentUser.uid).username!!
                    DatabaseImpl.getProfilePicture(localFile, currentUser.uid)
                        .addOnSuccessListener {
                            val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                            profileImage.setImageBitmap(bitmap)
                        }
                        .addOnFailureListener {
                            Log.d("Menu Fragment", "no profile image")
                        }
                }
            }
            view.findViewById<TextView>(R.id.username_text_view).text = username
        }

        rankButton = view.findViewById(R.id.ranking_list_button)
        rankButton.setOnClickListener {
            rankButton.alpha = 0.5F
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToRankingListFragment()
            )
        }

        myProfileButton = view.findViewById(R.id.profile_button)
        myProfileButton.setOnClickListener {
            myProfileButton.alpha = 0.5F
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToUserProfileFragment()
            )
        }

        newGameButton = view.findViewById(R.id.new_game_button)
        newGameButton.setOnClickListener {
            newGameButton.alpha = 0.5F
            view.findNavController().navigate(
                MenuFragmentDirections.actionMenuFragmentToPongActivity()
            )
        }
    }

    override fun onStart() {
        newGameButton.alpha = 1.0F
        rankButton.alpha = 1.0F
        myProfileButton.alpha = 1.0F
        super.onStart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun Fragment.setActivityTitle(title: String) {
        (activity as AppCompatActivity?)!!.supportActionBar?.title = title
    }
}
